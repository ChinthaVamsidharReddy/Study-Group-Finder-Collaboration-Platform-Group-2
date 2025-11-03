package com.infy.project.Controller;

import java.time.LocalDateTime;

import com.infy.project.Dto.PollDTO;

import com.infy.project.Dto.PollDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SocketPayload {
    
	private String type;      // "poll", "poll_vote", etc
    private int groupId;   // stringified group id
    private Object content;   // PollDTO or other
    
	public SocketPayload(String type, int groupId, Object content) {
		super();
		this.type = type;
		this.groupId = groupId;
		this.content = content;
	}

	public SocketPayload(String type2, Long groupId2, PollDTO dto) {
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
    
	
    


}
