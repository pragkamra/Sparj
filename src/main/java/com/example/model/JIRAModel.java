package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * JIRAModel contains coloumn name required in specified collection. 
 * 
 *
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */

@Document(collection = "JIRAInfo")
public class JIRAModel {
	@Id
    private String JiraName;
    private String priority;
    private String email;
    private String created;
    private String ProjName;
	public String getJiraName() {
		return JiraName;
	}

	public void setJiraName(String jiraName) {
		JiraName = jiraName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getCreated() {
		return created;
	}


	public void setCreated(String created) {
		this.created = created;
	}
	public void setProj(String ProjName) {
		this.ProjName = ProjName;
	}
	public String getProj() {
		return ProjName; 
	}


	@Override
	public String toString() {
		return "JIRAModel [JiraName=" + JiraName + ", priority=" + priority + ", email=" + email + ", created="
				+ created + "]";
	}

	


}
