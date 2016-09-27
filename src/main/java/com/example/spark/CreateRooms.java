package com.example.spark;


import java.io.IOException;
import org.springframework.stereotype.Service;

import com.ciscospark.Membership;
import com.ciscospark.Message;
import com.ciscospark.Room;
import com.ciscospark.Spark;
import com.ciscospark.Spark.Builder;
import com.ciscospark.SparkException;
import com.ciscospark.Webhook;
import com.example.SetAuthToken;
import com.example.dblayer.impl.UserServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Spring CreateRooms class call spark api to create rooms based on received room title. 
 * 
 *
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */

@Service
public class CreateRooms {
	public String roomID = null;
	public String roomIDAddmember =null;
	boolean create=true;
	/*SetAuthToken set = new SetAuthToken();
	String oAuthToken = set.oAuthToken;
*/
	String accessToken = "MjhkNmIwNzYtOWFjNy00ZWMwLWJmMGQtN2FlMWYyN2M4NGI5ZGFmYjMxNDQtYTE4";
	String botToken = "M2ZlOWI2YjEtYmMzYy00ZWMwLWJmZjAtYjMzMjcxMTdjMTVlNmM4ZTRhNTMtM2Fi";

	// Initialize the client
	Spark spark = Spark.builder()
			.baseUrl(URI.create("https://api.ciscospark.com/v1"))
			.accessToken(botToken)
			.build();
/*
	Spark spark1 = Spark.builder()
			.baseUrl(URI.create("https://api.ciscospark.com/v1/webhooks"))
			.accessToken(accessToken)
			.build();*/

	/**
	 * Method to create Spark room by received title. 
	 * 
	 *
	 * @param RoomTitle title name used to create spark room with this title
	 * @throws URISyntaxException 
	 * @return,
	 */	
	//Find roomID to post jira comment
	public void FindRoom(String roomname,String txt) throws IOException, URISyntaxException {
		// List the rooms that I'm in
		spark.rooms()
		.iterate()
		.forEachRemaining(room -> {
			if(room.getTitle().contains(roomname)){
				String roomID =room.getId();
				System.out.println("roonID from find room is"+roomID);
				postNewSparkMessage(roomID,txt);
			}
			//System.out.println(room.getTitle() + ", created " + room.getCreated() + ": " + room.getId());
		});}

	//Find roomID to add member
	public void FindRoom(String roomname) throws IOException, URISyntaxException {
		// List the rooms that I'm in
		spark.rooms()
		.iterate()
		.forEachRemaining(room -> {
			if(room.getTitle().contains(roomname)){
				roomIDAddmember =room.getId();
				System.out.println("roomID from find room add memeber is"+roomIDAddmember);
			}
			//System.out.println(room.getTitle() + ", created " + room.getCreated() + ": " + room.getId());
		});}

	public void CreateRoom(String RoomTitle) throws IOException, URISyntaxException {
		//Find if room with this title already created
		spark.rooms()
		.iterate()
		.forEachRemaining(room2 -> {
			if(room2.getTitle().contains(RoomTitle)){
				roomID =room2.getId();
				System.out.println("Room with this title already created");
				create=false;
			}
		});
		if(create){
			// Create a new room
			Room room = new Room();
			room.setTitle(RoomTitle);
			room = spark.rooms().post(room);
			System.out.println("Room created with title" + RoomTitle);

			// Get the roomID to set the filter in spark webhook
			spark.rooms()
			.iterate()
			.forEachRemaining(room1 -> {
				if(room1.getTitle().contains(RoomTitle)){
					roomID =room1.getId();
					System.out.println("roomID in create room is"+roomID);
				}
				//System.out.println(room.getTitle() + ", created " + room.getCreated() + ": " + room.getId());
			});

			//Create webhook for each room, to receive post msgs
			//For public domain
			URI uri = new URI("https://sparj-seemaraheja.c9users.io/sparj/sparkwebhook");
			Webhook web = new Webhook();
			web.setName(RoomTitle);
			web.setTargetUrl(uri);
			web.setResource("messages");
			web.setEvent("created");
			web.setFilter("roomId="+roomID);
			web = spark.webhooks().post(web);
			System.out.println("Corresponding sparkweb created with title" + RoomTitle);
		}
	}

	/**
	 * Method to Post a new Message in spark Room by taking roomId. 
	 * 
	 *
	 * @param roomId Id of room for which message posting is required
	 * @return,
	 */	
	//Get room ID and txt msg from findroom and post chat msg here
	public void postNewSparkMessage(String roomId,String txt) {
		// Post a text message to the room
		Message message = new Message();
		message.setRoomId(roomId);
		message.setText(txt);
		spark.messages().post(message);
	}

	/**
	 * Method to Add a new CoMember in spark Room by taking roomId. 
	 * 
	 *
	 * @param roomId Id of room for which message posting is required
	 * @return,
	 */

	public void addNewMemberRoom(String roomId, String email) {
		try{
			// Add a coworker to the room
			Membership membership = new Membership();
			membership.setRoomId(roomId);
			membership.setPersonEmail(email);
			spark.memberships().post(membership);
		}catch(SparkException e){
			if(e.getMessage().contains("409")){
				System.out.println("Member already present in the room");
			}else{
				e.printStackTrace();
			}
		}
	}

}
