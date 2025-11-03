package com.infy.project.Dto;


import lombok.Data;
import java.util.List;

@Data
public class PollVoteRequest {
    private Long pollId;
    private Long groupId;
    private Long userId;
    private List<Long> optionIds;
	public Long getPollId() {
		return pollId;
	}
	public void setPollId(Long pollId) {
		this.pollId = pollId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<Long> getOptionIds() {
		return optionIds;
	}
	public void setOptionIds(List<Long> optionIds) {
		this.optionIds = optionIds;
	}
    
    
    
}
