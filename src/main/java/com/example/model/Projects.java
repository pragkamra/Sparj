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

@Document(collection = "PROJECTS")
public class Projects {
	@Id
    private String key;
    private String name;
    private String id;
    private String projectLeadEmail;

    public String getProjectLead() {
		return projectLeadEmail;
	}

	public void setProjectLead(String projectLeadEmail) {
		this.projectLeadEmail = projectLeadEmail;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getid() {
		return id;
	}


	public void setId( String id) {
		this.id = id;
	}




	


}

