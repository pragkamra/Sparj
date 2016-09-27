package com.example.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.dblayer.impl.UserServiceImpl;
import com.example.dblayer.interfaces.UserServiceInterface;
import com.example.model.JIRAModel;
import com.example.model.JIRAModel1;
import com.example.model.Projects;
import com.example.spark.CreateRooms;

/**
 * Spring JIRARestController class to handle JIRA webhooks requests and customer login management requests. 
 * 
 *
 * The class will only return positive responses or raise Exception. It is not responsible of creating
 * Error response (e.g. HTTP 4XX, or 5XX)
 *
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/sparj")
@Configuration
@ComponentScan("com.example.dblayer.interfaces")
public class JIRARestController {
	static String RoomCreate,priority;

	@Autowired
	private UserServiceInterface userRepo;

	@Autowired
	private CreateRooms createRoom;


	/**
	 * Method to handle JIRA Webhook requests. 
	 * Based on priority of JIRA received it will create Spark rooms
	 * 
	 * Though the HTTP method is POST (create the object) but it patch the object based on the parameters
	 * Received in the JSON body.
	 *
	 * @param InputStream object representing JSON request body from which it will fetch priority of issue.
	 * @return,
	 */	
	@RequestMapping(value="/putwebhook" ,method=RequestMethod.POST,consumes="application/json")
	@ResponseBody
	public ResponseEntity<String> getResponse(InputStream incomingData) throws IOException {
		String payload;
		StringBuilder Builder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				Builder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + Builder.toString());
		payload = Builder.toString();
		userRepo.parseJIRAResponse(payload);
		return new ResponseEntity<String>("Successfully response received", HttpStatus.OK);
	}

	/**
	 * Method to handle Users Login requests. 
	 * It allows Users to login based on username and password
	 * 
	 *
	 * @param username username of user.
	 * @param password password of the user
	 * @return,
	 */	

	@RequestMapping(value="/login" ,method=RequestMethod.GET)	
	@ResponseBody
	public ResponseEntity<String> getMessageQueryParam(@RequestParam("username") String usr,@RequestParam("password") String pwd) throws UnknownHostException{
		System.out.println("user and pwd from login page is"+usr+ pwd);

		boolean result = userRepo.isValidUser(usr, pwd);
		if(result) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}else
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);		
	}


	/**
	 * Method to Register user on Page.
	 * 
	 *
	 * @param username username of the user trying to register.
	 * @param password password of the user trying to register.
	 * @param sparkusername username of the user registered on SPARK.
	 * @param sparkpassword password of the user registered on SPARK.
	 * @param jirausername username of the user registered on JIRA.
	 * @param jirapassword password of the user registered on JIRA.
	 * @return,
	 */	
	@RequestMapping(value="/register" ,method=RequestMethod.GET)	
	@ResponseBody
	public ResponseEntity<String> getMessageQueryParamRegister(@RequestParam("sparkusername") String sparkusername,
			@RequestParam("sparkpassword") String sparkpassword,@RequestParam("jirausername") String jirausername,@RequestParam("jirapassword") String jirapassword,@RequestParam("accessToken") String accessToken,@RequestParam("refreshToken") String refreshToken) throws UnknownHostException{
		System.out.println("user and pwd from login page is"+sparkusername+sparkpassword+jirausername+jirapassword);
		boolean result = userRepo.isValidUser(sparkusername, sparkpassword);
		if(result) 
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		else {
			userRepo.SaveUser(sparkusername,sparkpassword,jirausername,jirapassword,accessToken,refreshToken);
			return new ResponseEntity<String>(HttpStatus.OK);
		}
	}

	/**
	 * Method to get JIRA based on JIRA id stored in database. 
	 * Based on priority of JIRA name received it will fetch JIRA
	 * 
	 * Though the HTTP method is GETT (Fetch the object) 
	 *
	 * @param JiraName id or name of JIRA stored in database.
	 * @return,
	 */	
	/*@RequestMapping(value="/GETJIRA" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.TEXT_HTML_VALUE})
	@ResponseBody
	public ResponseEntity<String> getMessageQueryParam(@RequestParam("email") String email) throws UnknownHostException
	{	
		final String result = userRepo.FetchJira(email);
		if (result.equals("Not Found")) 
			return new ResponseEntity<String>("JIRA with this email id is not found", HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<String>(result, HttpStatus.OK);

	}*/
	@RequestMapping(value="/GETJIRA" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<List<JIRAModel>> getMessageQueryParam(@RequestParam("email") String email) throws UnknownHostException
	{	

		System.out.println("email in url is"+ email);
		List<JIRAModel> listdata =  userRepo.FetchJira(email);
		System.out.println("list data is "+listdata);
		//return new ResponseEntity<List>(listdata, HttpStatus.OK);
		return new ResponseEntity<List<JIRAModel>>(listdata, HttpStatus.OK); 

	}
	@RequestMapping(value="/GETPROJ" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<List<Projects>> getProjParam(@RequestParam("email") String email) throws UnknownHostException
	{	

		System.out.println("email in url is"+ email);
		List<Projects> listdata =  userRepo.FetchProjects(email);
		System.out.println("list data is "+listdata);
		//return new ResponseEntity<List>(listdata, HttpStatus.OK);
		return new ResponseEntity<List<Projects>>(listdata, HttpStatus.OK); 

	}
	@RequestMapping(value="/GETPROJDETAILS" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<String> getprojDetail() throws UnknownHostException
	{	


		String listdata=  userRepo.JiraPriority();
		System.out.println("list data is "+listdata);
		//return new ResponseEntity<List>(listdata, HttpStatus.OK);
		String result = "RESTService Successfully started..";
		return new ResponseEntity<String>(listdata, HttpStatus.OK);

	}
	@RequestMapping(value="/GETPROJJIRA" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<List<JIRAModel>> getprojParam(@RequestParam("projname") String projname) throws UnknownHostException
	{	

		System.out.println("email in url is"+ projname);
		List<JIRAModel> listdata =  userRepo.FetchProjJira(projname);
		System.out.println("list data is "+listdata);
		//return new ResponseEntity<List>(listdata, HttpStatus.OK);
		return new ResponseEntity<List<JIRAModel>>(listdata, HttpStatus.OK); 

	}

	//get messages from DB
	@RequestMapping(value="/GETMessages" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<List<JIRAModel1>> getMessageQueryParam1(@RequestParam("ID") String ID) throws UnknownHostException
	{	

		List<JIRAModel1> listdata1 = userRepo.FetchMessage(ID);
		return new ResponseEntity<List<JIRAModel1>>(listdata1, HttpStatus.OK);

	}


	/**
	 * Method to handle JIRA Webhook requests. 
	 * Based on priority of JIRA received it will create Spark rooms
	 * 
	 * Though the HTTP method is POST (create the object) but it patch the object based on the parameters
	 * Received in the JSON body.
	 *
	 * @param InputStream object representing JSON request body from which it will fetch priority of issue.
	 * @return,
	 */	

	@RequestMapping(value="/verify" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.TEXT_PLAIN_VALUE})
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<String> verifyRESTService(InputStream incomingData) {

		String result = "RESTService Successfully started..";
		return new ResponseEntity<String>(result, HttpStatus.OK);

	}

	/**
	 * Method to create room based on title
	 * 
	 * 
	 * Though the HTTP method is GETT (Fetch the object) 
	 *
	 * @param titlenameid titlename of the spark room.
	 * @return,
	 */	
	@RequestMapping(value="/createSparkRoom" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.TEXT_HTML_VALUE})
	@ResponseBody
	public ResponseEntity<String> createSparkRoom(@RequestParam("titlename") String titleName) throws UnknownHostException
	{	
		try {
			createRoom.CreateRoom(titleName);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		};
		return new ResponseEntity<String>("Room Created", HttpStatus.OK);
	}

	//Not in use
	/*@RequestMapping(value="/PostToSpark" ,method=RequestMethod.GET,produces={org.springframework.http.MediaType.TEXT_HTML_VALUE})
	@ResponseBody
	public ResponseEntity<String> PostToSpark(@RequestParam("titlename") String titleName) throws UnknownHostException, IOException
	{	
		 createRoom.postNewSparkMessage(titleName);
		 return new ResponseEntity<String>("Message posted", HttpStatus.OK);
	}*/

	@RequestMapping(value="/sparkwebhook" ,method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> getResponseFromSpark(InputStream incomingData) throws IOException {
		String payload;
		StringBuilder Builder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				Builder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		//System.out.println("Data Received: " + Builder.toString());
		payload = Builder.toString();
		System.out.println("Response from Spark Webhook"+payload);
		userRepo.parseSPARKResponse(payload);
		/*        JsonNode node = mapper.readTree(payload);
        String sparkmessageId = node.findValue("data").findValue("id").asText();
        Utility.writeInFile("sparkmessageId--" + sparkmessageId + "\n");*/


		return new ResponseEntity<String>("Successfully response received", HttpStatus.OK);
	}

	//From dashboard chat room to spark chat room
	@RequestMapping(value="/dashboardtospark" ,method=RequestMethod.GET)	
	@ResponseBody
	public ResponseEntity<String> getMessageQueryParam2(@RequestParam("roomname") String roomname,@RequestParam("txt") String txt) throws IOException, URISyntaxException{
		System.out.println("room name and txt from dashboard is"+roomname+ txt);

		createRoom.FindRoom(roomname, txt);		
		return new ResponseEntity<String>("message posted in spark chat room", HttpStatus.OK);
	}

	//From dashboard chat room to JIRA comment
	@RequestMapping(value="/dashboardtojira" ,method=RequestMethod.GET)	
	@ResponseBody
	public ResponseEntity<String> getMessageQueryParam3(@RequestParam("JiraIssueID") String JiraIssueID,@RequestParam("msgTxt") String msgTxt) throws IOException, URISyntaxException{
		String JiraIssueID2[] =JiraIssueID.split("-");
		JiraIssueID = JiraIssueID2[0];
		System.out.println("jira issue ID and msgTxt from dashboard is"+JiraIssueID+ msgTxt);

		userRepo.postJiraComment(JiraIssueID, msgTxt);
		return new ResponseEntity<String>("comment posted in jira", HttpStatus.OK);
	}
	
   
}