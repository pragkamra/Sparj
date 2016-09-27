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

@Document(collection = "Messages")
public class JIRAModel1 {
	@Id
	private String id;
    private String name;
    private String message;
    private String createdAt;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "JIRAModel1 [id=" + id + ", name=" + name + ", message=" + message + ", createdAt=" + createdAt + "]";
	}
    
	
    
	

}
