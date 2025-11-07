package com.infy.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TypingNotification {
    private Long groupId;
    private Long userId;
    private String userName;
    private String type; // "typing" or "typing_stop"
	public TypingNotification(Long groupId, Long userId, String userName, String type) {
		super();
		this.groupId = groupId;
		this.userId = userId;
		this.userName = userName;
		this.type = type;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    
    
    
}