package com.infy.project.Dto;

import lombok.Data;
import java.util.*;

@Data
public class PollOptionDTO {
    private Long id;
    private String text;
    private List<Long> votes = new ArrayList<>(); // ✅ accept simple list of userIds
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<Long> getVotes() {
		return votes;
	}
	public void setVotes(List<Long> votes) {
		this.votes = votes;
	}

	
	
	
    
    
}
