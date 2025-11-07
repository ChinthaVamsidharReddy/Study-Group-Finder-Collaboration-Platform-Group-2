// DTO class
package com.infy.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class GroupJoinRequestDTO {
    private Long memberId;      // ID from group_members table
    private Long userId;        // ID from users table
    private String userName;    // from users table
    private String userMajor;   // from users table
    private String requestedAt; // timestamp
	public GroupJoinRequestDTO(Long memberId, Long userId, String userName, String userMajor, String requestedAt) {
		super();
		this.memberId = memberId;
		this.userId = userId;
		this.userName = userName;
		this.userMajor = userMajor;
		this.requestedAt = requestedAt;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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
	public String getUserMajor() {
		return userMajor;
	}
	public void setUserMajor(String userMajor) {
		this.userMajor = userMajor;
	}
	public String getRequestedAt() {
		return requestedAt;
	}
	public void setRequestedAt(String requestedAt) {
		this.requestedAt = requestedAt;
	}
    
    
    
    
}
