package com.infy.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class PollVoteEvent {
    private Long groupId;
    private String messageId;
    private String pollId;
    private Long userId;
    private List<Long> optionIds;
	public PollVoteEvent(Long groupId, String messageId, String pollId, Long userId, List<Long> optionIds) {
		super();
		this.groupId = groupId;
		this.messageId = messageId;
		this.pollId = pollId;
		this.userId = userId;
		this.optionIds = optionIds;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getPollId() {
		return pollId;
	}
	public void setPollId(String pollId) {
		this.pollId = pollId;
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