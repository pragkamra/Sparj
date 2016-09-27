package com.example.dblayer.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.httpclient.HttpException;

import com.example.model.JIRAModel;
import com.example.model.JIRAModel1;
import com.example.model.Projects;

public interface UserServiceInterface {
	public boolean isValidUser(String username, String password);
	public void SaveUser(String sparkusername, String sparkpassword,String jirausername, String jirapassword,String accessToken,String refreshToken);
	public void parseJIRAResponse(String payload);
	public void parseSPARKResponse(String payload) throws  IOException;
	public void InsertJira();
	public void InsertMsgToDB();
	public  List<JIRAModel> FetchProjJira( String projname);
	public List<JIRAModel>  FetchJira(String JiraName);
	public List<JIRAModel1>  FetchMessage(String ID);
	public List<Projects> FetchProjects(String email);
	public void postJiraComment(String JiraIssueID, String msgTxt);
	public String JiraPriority();
	
}
