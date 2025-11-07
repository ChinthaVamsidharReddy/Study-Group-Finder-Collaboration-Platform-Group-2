package com.infy.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReactionEvent {
    private Long groupId;
    private String messageId;
    private String emoji;
    private Long userId;
	public ReactionEvent(Long groupId, String messageId, String emoji, Long userId) {
		super();
		this.groupId = groupId;
		this.messageId = messageId;
		this.emoji = emoji;
		this.userId = userId;
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
	public String getEmoji() {
		return emoji;
	}
	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
    
    
}