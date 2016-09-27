package com.example.dblayer.impl;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ciscospark.SparkException;
import com.example.SetAuthToken;
import com.example.configuration.MongoConfig;
import com.example.dblayer.interfaces.UserServiceInterface;
import com.example.model.JIRAModel;
import com.example.model.JIRAModel1;
import com.example.model.Projects;
import com.example.model.User;
import com.example.spark.CreateRooms;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.ICredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.Watches;



/**
 * UserServiceImpl used to perform all DB operations. 
 * 
 *
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */



@Repository
public class UserServiceImpl implements UserServiceInterface{
	public static String roomname=null,txt=null,priority=null,sparkMsg="",JiraMsg="",
			IssueKey,emailAdd,createdAt,response,summary,createdAtJira,id,createdAtspark,roomname1,txt1,createdAtJira1,NewProj,key,projectLeadEmail;;
	String assignee="",type,assigneefromUpdate,roomID;
	static StringBuilder finaljson;

	@Autowired
	private CreateRooms createRoom;

	public static String RoomCreate,priority1,msgID;
	ApplicationContext ctx = 
			new AnnotationConfigApplicationContext(MongoConfig.class);
	MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

	public boolean isValidUser(String sparkusername, String sparkpassword) {

		System.out.println("spark username and password is" +sparkusername +":" +sparkpassword);
		try {
			Query searchUserQuery = new Query(Criteria.where("sparkusername").is(sparkusername));
			User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
			System.out.println("2. find - savedUser : " + savedUser.getSparkusername());
			Query searchUserQuery1 = new Query(Criteria.where("sparkpassword").is(sparkpassword));
			User savedPassword = mongoOperation.findOne(searchUserQuery1, User.class);
			System.out.println("2. find - savedUser : " + savedPassword.getSparkpassword());
			if (savedUser != null && savedPassword!=null) {
				return true;
			}
		}
		catch (NullPointerException nullPointer) {
			System.out.println("Exception in getting data from Mongo DB");
		}

		return false;		

	}

	public void SaveUser(String sparkusername, String sparkpassword,String jirausername, String jirapassword,String accessToken,String refreshToken) {
		System.out.println("spark username and password is" +sparkusername +":" +sparkpassword);
		User user = new User(sparkusername,sparkpassword);
		user.setSparkusername(sparkusername);
		user.setSparkpassword(sparkpassword);
		user.setJirausername(jirausername);
		user.setJirapassword(jirapassword);
		user.setaccessToken(accessToken);
		user.setrefreshToken(refreshToken);
		mongoOperation.save(user);
		System.out.println("User info saved");
	}
	//Handle webhook response from JIRA here
	public void parseJIRAResponse(String payload)  {
		//System.out.println("On event Payload: "+payload+"\n");
		JSONObject jsonObj = new JSONObject(payload);
		String webhookEvent=jsonObj.getString("webhookEvent");
		if(webhookEvent.equals("jira:issue_created")){
			JSONObject jsonObj2= (JSONObject) jsonObj.get("issue");
			JSONObject jsonObj3= (JSONObject) jsonObj2.get("fields");
			JSONObject jsonObj4= (JSONObject) jsonObj3.get("priority");
			JSONObject jsonOBJ1= (JSONObject) jsonObj3.get("reporter");
			JSONObject jsonAssignee = null;
			try{
				jsonAssignee= (JSONObject) jsonObj3.get("assignee");
			}catch(ClassCastException e){
				System.out.println("Issue not assigned at the time of creation");
			}
			if(jsonAssignee!=null){
				assignee = jsonAssignee.getString("emailAddress");
				System.out.println("assignee is "+assignee);
			}

			emailAdd = jsonOBJ1.getString("emailAddress");
			createdAt = jsonObj3.getString("created");
			priority1=jsonObj4.getString("name");
			summary=jsonObj3.getString("summary");
			RoomCreate=jsonObj2.getString("id")+"-"+summary;
			System.out.println("room name,priority,email,createdAt,summary is "+RoomCreate +priority1+emailAdd+createdAt+summary);
			if(RoomCreate!=null)
			{
				//CreateRooms createRoom = new CreateRooms();
				try {
					createRoom.CreateRoom(RoomCreate);
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}          
			}

			//Add reporter in spark room
			String roomID1 = createRoom.roomID;
			createRoom.addNewMemberRoom(roomID1, emailAdd);

			//Add assignee in spark room
			if(assignee!=null&&!assignee.equals("")){
				String roomID = createRoom.roomID;
				System.out.println("room ID and assignee before add is"+roomID+assignee);

				createRoom.addNewMemberRoom(roomID, assignee);
			}		

			//Insert jira info in mongo DB
			InsertJira();
		}
		else if(webhookEvent.equals("project_created")){
			try{
				
				
				JSONObject jsonObj2= (JSONObject) jsonObj.get("project");
				JSONObject jsonObj3= (JSONObject) jsonObj2.get("projectLead");
				NewProj = jsonObj2.getString("name");
				id=jsonObj2.get("id").toString();
		
				key=jsonObj2.getString("key");
				projectLeadEmail=jsonObj3.getString("emailAddress");
				InsertProjToDB();
				System.out.println("Hello vivek new proj created"+NewProj);
			/*	 IssueKey = jsonObj2.getString("key");
				summary=jsonObj3.getString("summary");
				RoomCreate=jsonObj2.getString("id")+"-"+summary;
				
				JSONObject jsonObj6= (JSONObject) jsonObj.get("changelog");
				
				JSONArray jsonObj7 = jsonObj6.getJSONArray("items");
				System.out.println("array len is"+jsonObj7.length());
				for(int i=0;i<jsonObj7.length();i++){
					JSONObject childJSONObject = jsonObj7.getJSONObject(i);
					   priority  = childJSONObject.getString("toString");
					   System.out.println("pri in issue_updated us"+priority);   
				}
				if(priority.equals("High")||priority.equals("Highest")){
					try {
						createRoom.CreateRoom(RoomCreate);
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}  
				}*/
				}catch (JSONException e) {
					e.printStackTrace();
					//e.printStackTrace();
				} 
			//If webhook received for a comment creation
			}
		//if priority changed to highest/high for an already created issue
		else if(webhookEvent.equals("jira:issue_updated")){
			try{

				JSONObject jsonObj2= (JSONObject) jsonObj.get("issue");
				JSONObject jsonObj3= (JSONObject) jsonObj2.get("fields");
				JSONObject jsonOBJ1= (JSONObject) jsonObj3.get("reporter");
				createdAt = jsonObj3.getString("created");
				emailAdd = jsonOBJ1.getString("emailAddress");
				IssueKey = jsonObj2.getString("key");
				summary=jsonObj3.getString("summary");
				RoomCreate=jsonObj2.getString("id")+"-"+summary;

				JSONObject jsonObj6= (JSONObject) jsonObj.get("changelog");

				JSONArray jsonObj7 = jsonObj6.getJSONArray("items");
				System.out.println("array len is"+jsonObj7.length());
				for(int i=0;i<jsonObj7.length();i++){
					JSONObject childJSONObject = jsonObj7.getJSONObject(i);
					type  = childJSONObject.getString("field");
					priority  = childJSONObject.getString("toString");
					assigneefromUpdate  = childJSONObject.getString("to");
					System.out.println("type and pri in issue_updated us"+type + priority);   
				}
				if(type.equals("priority")||priority.equals("High")||priority.equals("Highest")){
					try {
						//create room if priority changes from medium to high/highest
						createRoom.CreateRoom(RoomCreate);
						
						//Insert jira info in mongo DB
						InsertJira();
						
						//Add reporter in spark room for the above room created
						 roomID = createRoom.roomID;
						createRoom.addNewMemberRoom(roomID, emailAdd);
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}  
				}else if(type.equals("assignee")){
					try {
						createRoom.FindRoom(RoomCreate);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JSONObject jsonAssignee= (JSONObject) jsonObj3.get("assignee");
					assignee = jsonAssignee.getString("emailAddress");
					 roomID = createRoom.roomIDAddmember;
					//	
					System.out.println("room id and assignee"+roomID+assignee);
					createRoom.addNewMemberRoom(roomID, assignee);
				}
				else if(type.equals("resolution")){
					try {
						createRoom.FindRoom(RoomCreate);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 roomID = createRoom.roomIDAddmember;
					//	
					System.out.println("room id in resolution is "+roomID);
					createRoom.postNewSparkMessage(roomID, "This issue has been resolved");
				}

			}catch (JSONException e) {
				System.out.println("inside catch");
				//e.printStackTrace();
			} finally{
				//Finally add watchers to the spark room
				try {
					createRoom.FindRoom(RoomCreate);
					roomID = createRoom.roomIDAddmember;
					getWatchers(IssueKey,roomID);
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JiraException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			//If webhook received for a comment creation
		}else if(webhookEvent.equals("comment_created")){
			System.out.println("Evnt name and type is"+webhookEvent);
			/*JSONObject jsonObj5= (JSONObject) jsonObj.get("issue");
			roomname=jsonObj5.getString("key");*/

			JSONObject jsonObj6= (JSONObject) jsonObj.get("comment");
			txt=jsonObj6.getString("body");
			createdAtJira=jsonObj6.getString("created");

			//check msg duplicacy
			JiraMsg = txt;
			String temproomname=jsonObj6.getString("self");
			String temproomname1[] =temproomname.split("/");
			roomname=temproomname1[7];
			System.out.println("room name and txt from jira comment are"+roomname+txt);

			System.out.println("messages from spark and jira in spark "+ sparkMsg+JiraMsg);
			if(!sparkMsg.equals(JiraMsg)){
				try {
					createRoom.FindRoom(roomname,txt);

					//Add message to Mongo DB	
					InsertMsgToDB();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				sparkMsg="";
			}
		}

	}

	//Insert JIRAs created in mongo DB
	public void InsertJira() {
		String priority = priority1;
		String JiraName= RoomCreate;
		String email= emailAdd;
		String created= createdAt;
		String projname=NewProj;
		System.out.println("Name,priority,email,created in insert jira is"+JiraName+priority+email+created);
		try {	    	
			//JIRAModel jira = new JIRAModel(JiraName,priority,email,created);
			JIRAModel jira = new JIRAModel();
			jira.setJiraName(JiraName);
			jira.setPriority(priority);
			jira.setEmail(email);
			jira.setCreated(created);
			jira.setProj(projname);
			
			mongoOperation.save(jira);
		}catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public List<JIRAModel> FetchJira(String email){

		System.out.println("JIRA email is" +email);
		List<JIRAModel> savedJIRA =new ArrayList<JIRAModel>();
		try {
			Query searchUserQuery = new Query(Criteria.where("email").is(email));
			//sort JIRAs based on created date and fecth only latest five
			searchUserQuery.with(new Sort(Sort.Direction.DESC, "created"));
			//searchUserQuery.limit(5);
			savedJIRA = mongoOperation.find(searchUserQuery, JIRAModel.class);
			//System.out.println("2. find - savedJIRA : " + savedJIRA .getemail());
			/*if (savedJIRA != null) {
				return savedJIRA.getJiraName()+savedJIRA.getPriority()+savedJIRA.getemail()+savedJIRA.getcreated();
			}*/
		}
		catch (NullPointerException nullPointer) {
			System.out.println("Exception in getting data from Mongo DB");
		}

		return savedJIRA;

	}

	public String JiraPriority()
	{
	
		long savedJIRA = 0;
		JSONObject obj = null;
		try {
			Query searchUserQuery = new Query(Criteria.where("email").is("seema.makkar@gmail.com"));
		
		
			//sort JIRAs based on created date and fecth only latest five
			 searchUserQuery.addCriteria(Criteria.where("priority").is("Highest"));
		
			savedJIRA = mongoOperation.count(searchUserQuery, JIRAModel.class);
			System.out.println("Numberof highest result"+ savedJIRA);
			Query searchUserQuery1 = new Query(Criteria.where("email").is("seema.makkar@gmail.com"));
			
			
			//sort JIRAs based on created date and fecth only latest five
			 searchUserQuery1.addCriteria(Criteria.where("priority").is("High"));
		
			long savedJIRA1 = mongoOperation.count(searchUserQuery1, JIRAModel.class);
			System.out.println("Numberof highest result"+ savedJIRA1);
			 obj = new JSONObject();

		      obj.put("Highest", new Long(savedJIRA));
		      obj.put("High", new Long(savedJIRA1));
		
		  
			//System.out.println("2. find - savedJIRA : " + savedJIRA .getemail());
			/*if (savedJIRA != null) {
				return savedJIRA.getJiraName()+savedJIRA.getPriority()+savedJIRA.getemail()+savedJIRA.getcreated();
			}*/
		}
		catch (NullPointerException nullPointer) {
			System.out.println("Exception in getting data from Mongo DB");
		}
		return obj.toString();

	

	       
	      /*  Issue issue = jira.getIssue(JiraIssueID);
	        issue.addComment(msgTxt);
	        
	        roomname1=JiraIssueID;
			txt1=msgTxt;
			createdAtJira1=createdAtspark;
	        //Add message to Mongo DB
			InsertMsgToDBSpark();*/
		
	
		
	}
	public List<JIRAModel> FetchProjJira(String ProjName){
		
		System.out.println("JIRA email is" +ProjName);
		List<JIRAModel> savedJIRA =new ArrayList<JIRAModel>();
		try {
			Query searchUserQuery = new Query(Criteria.where("ProjName").is(ProjName));
	
			searchUserQuery.with(new Sort(Sort.Direction.DESC, "created"));
			//searchUserQuery.limit(5);
			savedJIRA = mongoOperation.find(searchUserQuery, JIRAModel.class);
			//System.out.println("2. find - savedJIRA : " + savedJIRA .getemail());
			/*if (savedJIRA != null) {
				return savedJIRA.getJiraName()+savedJIRA.getPriority()+savedJIRA.getemail()+savedJIRA.getcreated();
			}*/
		}
		catch (NullPointerException nullPointer) {
			System.out.println("Exception in getting data from Mongo DB");
		}

		return savedJIRA;
		
  }
	public void InsertProjToDB() {
		
		System.out.println("Name,priority,email,created in insert jira is"+key+id+NewProj);
		try {	    	
			//JIRAModel jira = new JIRAModel(JiraName,priority,email,created);
			Projects proj = new Projects();
	 proj.setKey(key);
	 proj.setId(id);
	 proj.setName(NewProj);
	 proj.setProjectLead(projectLeadEmail);
	 System.out.print("PROJECT details"+key+id+NewProj+"\n");
			mongoOperation.save(proj);
		}catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public List<Projects> FetchProjects(String email){
		
		//System.out.println("JIRA name is" +name);
		List<Projects> savedPROJ =new ArrayList<Projects>();
		try {
			Query searchUserQuery = new Query(Criteria.where("projectLeadEmail").is(email));
			
			savedPROJ = mongoOperation.find(searchUserQuery, Projects.class);
		}
		catch (NullPointerException nullPointer) {
			System.out.println("Exception in getting data from Mongo DB");
		}

		return savedPROJ;
		
  }

	public void InsertMsgToDB() {
		String Name = roomname;
		String message= txt;
		String CreatedAt= createdAtJira;

		System.out.println("Name,message,created in insert jira is"+Name+message+CreatedAt);
		try {	    	
			//JIRAModel jira = new JIRAModel(JiraName,priority,email,created);
			JIRAModel1 jira1 = new JIRAModel1();
			jira1.setName(Name);
			jira1.setMessage(message);
			jira1.setCreatedAt(CreatedAt);
			mongoOperation.save(jira1);
		}catch (MongoException e) {
			if(e.getMessage().contains("Timed out after 10000 ms")){
				System.out.println("Mongo DB server not running, data not saved in DB");
			}
		}
	}

	public List<JIRAModel1> FetchMessage(String name){

		System.out.println("JIRA name is" +name);
		List<JIRAModel1> savedJIRA =new ArrayList<JIRAModel1>();
		try {
			Query searchUserQuery = new Query(Criteria.where("Name").is(name));

			savedJIRA = mongoOperation.find(searchUserQuery, JIRAModel1.class);
		}
		catch (NullPointerException nullPointer) {
			System.out.println("Exception in getting data from Mongo DB");
		}

		return savedJIRA;

	}

	//Handle spark webhook response here
	public void parseSPARKResponse(String payload) throws IOException  {
		String oAuthToken = "MjhkNmIwNzYtOWFjNy00ZWMwLWJmMGQtN2FlMWYyN2M4NGI5ZGFmYjMxNDQtYTE4";
		String JiraIssueID, msgTxt;
		//System.out.println("On event Payload: "+payload+"\n");
		JSONObject jsonObj = new JSONObject(payload);
		JSONObject jsonObj2= (JSONObject) jsonObj.get("data");
		msgID=jsonObj2.getString("id");

		System.out.println("spark msg id is" +msgID);
		String URL="https://api.ciscospark.com/v1/messages/"+msgID;
		HttpClient client = new HttpClient();
		HttpClient client1 = new HttpClient();
		final GetMethod get = new GetMethod(URL);
		final PostMethod post= new PostMethod(URL);
		post.setRequestHeader ("Authorization", "Bearer "+oAuthToken);

		client.executeMethod(post);

		try {
			get.setRequestHeader("Authorization", "Bearer "+oAuthToken);

			client1.executeMethod(get);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			IOUtils.copy(get.getResponseBodyAsStream(), baos);

			System.out.println("response"+baos.toString());
			JSONObject jsonObj1 = new JSONObject(baos.toString());
			msgTxt=jsonObj1.getString("text");
			createdAtspark = jsonObj1.getString("created");
			System.out.println("msg text is" +msgTxt);

			//check message duplicacy
			sparkMsg =msgTxt;

			String roomID=jsonObj1.getString("roomId");

			//get jira issue key from spark response	
			String URL1="https://api.ciscospark.com/v1/rooms/"+roomID;
			final GetMethod get1 = new GetMethod(URL1);
			try {
				get1.setRequestHeader("Authorization", "Bearer "+oAuthToken);
				client1.executeMethod(get1);

				ByteArrayOutputStream baos1 = new ByteArrayOutputStream();

				IOUtils.copy(get1.getResponseBodyAsStream(), baos1);

				System.out.println("response"+baos1.toString());
				JSONObject jsonObj3 = new JSONObject(baos1.toString());

				String JiraIssueID1=jsonObj3.getString("title");
				String JiraIssueID2[] = JiraIssueID1.split("-");
				JiraIssueID=JiraIssueID2[0];

				System.out.println("jira id from spark is"+JiraIssueID);

				System.out.println("messages from spark and jira in jira "+ sparkMsg+JiraMsg);
				if(!sparkMsg.equals(JiraMsg)){
					//from spark chat to jira comment 
					postJiraComment(JiraIssueID,msgTxt);
				}else{
					JiraMsg="";
				}}finally {
					get1.releaseConnection();
				}

		}finally {
			get.releaseConnection();
		}

	}
	//Post comments to JIRA from spark
	public void postJiraComment(String JiraIssueID, String msgTxt){
		try{
			BasicCredentials creds = new BasicCredentials("seema.makkar@gmail.com", "247780india");
			JiraClient jira = new JiraClient("https://sparjaricent.atlassian.net", creds);
			Issue issue = jira.getIssue(JiraIssueID);
			issue.addComment(msgTxt);

			roomname1=JiraIssueID;
			txt1=msgTxt;
			createdAtJira1=createdAtspark;
			//Add message to Mongo DB
			InsertMsgToDBSpark();

		}catch (JiraException  ex) {
			System.err.println(ex.getMessage());

			if (ex.getCause() != null)
				System.err.println(ex.getCause().getMessage());
		}
	}

	//Get Watchers from JIRA
	public void getWatchers(String IssueKey,String roomID) throws HttpException, IOException, JiraException{
		System.out.println("inside get watchers,issue key and roomid are "+IssueKey+roomID);
		String URL="https://sparjaricent.atlassian.net/rest/api/2/issue/"+IssueKey+"/watchers";
		HttpClient client = new HttpClient();

		String pwd ="admin:247780india";
		byte[] encodedBytes = Base64.encodeBase64(pwd.getBytes());
		//System.out.println("encodedBytes " + new String(encodedBytes));
		GetMethod get = new GetMethod(URL);
		get.setRequestHeader ("Authorization", "Basic "+ new String(encodedBytes));
		client.executeMethod(get);
		//System.out.println("psot status cod id"+get.getStatusCode());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(get.getResponseBodyAsStream(), baos);
		System.out.println("response in get watchers is "+baos.toString());
		
		JSONObject jsonObj = new JSONObject(baos.toString());
		JSONArray jsonObj1 = jsonObj.getJSONArray("watchers");
		System.out.println("array len in get watchers is"+jsonObj1.length());
		for(int i=0;i<jsonObj1.length();i++){
			JSONObject childJSONObject = jsonObj1.getJSONObject(i);
			String getWatchersEmail  = childJSONObject.getString("emailAddress");
			System.out.println("getwatchers email is "+getWatchersEmail); 
			createRoom.addNewMemberRoom(roomID, getWatchersEmail);
		}
		
	}

	public void InsertMsgToDBSpark() throws JiraException {
		String Name = roomname1;
		String message= txt1;
		String CreatedAt= createdAtJira1;

		System.out.println("Name,message,created in insert jira spark is"+Name+message+CreatedAt);
		try {	    	
			//JIRAModel jira = new JIRAModel(JiraName,priority,email,created);
			JIRAModel1 jira1 = new JIRAModel1();
			jira1.setName(Name);
			jira1.setMessage(message);
			jira1.setCreatedAt(CreatedAt);
			mongoOperation.save(jira1);

		}catch (MongoException e) {
			if(e.getMessage().contains("Timed out after 10000 ms")){
				System.out.println("Mongo DB server not running, data not saved in DB");
			}
		}

	}

	/*@Override
	public void SaveUser(String sparkusername, String sparkpassword, String jirausername,
			String jirapassword) {
		// TODO Auto-generated method stub

	}*/
	

}
