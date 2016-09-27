package com.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.UnknownHostException;

import javax.xml.ws.Response;

import org.apache.catalina.core.ApplicationContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.controller.JIRARestController;
import com.example.dblayer.interfaces.UserServiceInterface;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Sparj.class)
public class SparjTests {
	private MockMvc mockMvc;
	 @Autowired
		private UserServiceInterface userRepo;
	 @Autowired
		private JIRARestController rest;

	@Test
	public void contextLoads() {
		boolean flag = userRepo.isValidUser("vinay.yadav@aricent.com", "zxcv@12345678");
		
	System.out.println("flag returned is "+flag);	
	}
	

	/*@Test
	public void testGetRoles() throws Exception {
		mockMvc.perform(post("/verify")
	                .contentType(MediaType.TEXT_PLAIN_VALUE))
	                .andExpect(status().isOk());
	}
	*/

}
