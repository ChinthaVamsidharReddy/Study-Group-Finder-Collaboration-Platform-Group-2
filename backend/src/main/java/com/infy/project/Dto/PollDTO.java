package com.infy.project.Dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class PollDTO {
    private Long id;
    private String question;
    private List<PollOptionDTO> options;  // ✅ must match frontend "options"
    private boolean allowMultiple;
    private boolean anonymous;
    private Instant createdAt;
    private int totalVotes;
    private int groupId;  // ✅ include this if it’s sent from frontend
    
    
    // ✅ Add these two new fields
    private Long creatorId;
    private String creatorName;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<PollOptionDTO> getOptions() {
		return options;
	}
	public void setOptions(List<PollOptionDTO> options) {
		this.options = options;
	}
	public boolean isAllowMultiple() {
		return allowMultiple;
	}
	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	public int getTotalVotes() {
		return totalVotes;
	}
	public void setTotalVotes(int totalVotes) {
		this.totalVotes = totalVotes;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	
	
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public PollDTO(Long id, String question, List<PollOptionDTO> options, boolean allowMultiple, boolean anonymous,
			Instant createdAt, int totalVotes, int groupId,Long creatorId, String creatorName) {
		super();
		this.id = id;
		this.question = question;
		this.options = options;
		this.allowMultiple = allowMultiple;
		this.anonymous = anonymous;
		this.createdAt = createdAt;
		this.totalVotes = totalVotes;
		this.groupId = groupId;
		this.creatorId = creatorId;
        this.creatorName = creatorName;
	}
	public PollDTO() {
		super();
	}
	@Override
	public String toString() {
		return "PollDTO [id=" + id + ", question=" + question + ", options=" + options + ", allowMultiple="
				+ allowMultiple + ", anonymous=" + anonymous + ", createdAt=" + createdAt + ", totalVotes=" + totalVotes
				+ ", groupId=" + groupId + "]";
	}
	
	
	
	
	
    
    
    
}
