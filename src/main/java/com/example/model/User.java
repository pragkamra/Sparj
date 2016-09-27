package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User contains coloumn name required in specified collection. 
 * 
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */

@Document(collection = "users")
public class User {
	@Id
    private String username;
    private String password;
    private String sparkusername;
    private String sparkpassword;
    private String jirausername;
    private String jirapassword;
    private String accessToken;
    private String refreshToken;
    
    
    public User() {}

    public User(String userName , String password) {
        this.username = userName;
        this.password = password;
    }

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSparkusername() {
		return sparkusername;
	}

	public void setSparkusername(String sparkusername) {
		this.sparkusername = sparkusername;
	}

	public String getSparkpassword() {
		return sparkpassword;
	}

	public void setSparkpassword(String sparkpassword) {
		this.sparkpassword = sparkpassword;
	}

	public String getJirausername() {
		return jirausername;
	}

	public void setJirausername(String jirausername) {
		this.jirausername = jirausername;
	}

	public String getJirapassword() {
		return jirapassword;
	}

	public void setJirapassword(String jirapassword) {
		this.jirapassword = jirapassword;
	}
	
	public String getaccessToken() {
		return accessToken;
	}

	public void setaccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getrefreshToken() {
		return refreshToken;
	}

	public void setrefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	


}
