package com.infy.project.Dto;

import lombok.Data;

@Data
public class PollVoteDTO {
    private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
    
    
}
