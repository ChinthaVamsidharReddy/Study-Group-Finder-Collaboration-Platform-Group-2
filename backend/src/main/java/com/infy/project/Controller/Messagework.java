package com.infy.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.project.Service.ChatService;
import com.infy.project.Dto.ChatMessageDto;

@RestController

@RequestMapping("/api/chat")
public class Messagework {

	@Autowired
	 private ChatService chatService;
	
	 @GetMapping("/messages/{groupId}")
	 public List<ChatMessageDto> getMessagesByGroup(@PathVariable Long groupId) {
	     System.out.println("📜 Fetching old messages for group " + groupId);
	     System.out.println("\n\n\n\n\n\n"+chatService.getMessagesByGroupId(groupId)+"\n\n\n\n");
	     return chatService.getMessagesByGroupId(groupId);
	 }
}
