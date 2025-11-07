package com.infy.project.Dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageDto {
    private Long id;
    private Long groupId;
    private Long senderId;
    private String senderName;
    private String content;
    private String type;         // "text", "file", "poll"
    private String fileUrl;      // file-only
    private String fileType;     // mime
    private Long fileSize;       // optional size
    private LocalDateTime timestamp;

    private List<Long> deliveredBy = new ArrayList<>();
    private List<Long> readBy = new ArrayList<>();
    private Integer totalRecipients; // excluding sender
    private Long replyToId;
    private Boolean edited;
    private Boolean deleted;

    // ✅ No-args constructor
    public ChatMessageDto() {
    }

    // ✅ All-args constructor (used in Service mapping)
    public ChatMessageDto(Long id, Long groupId, Long senderId, String senderName, String content, LocalDateTime timestamp) {
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public List<Long> getDeliveredBy() { return deliveredBy; }
    public void setDeliveredBy(List<Long> deliveredBy) { this.deliveredBy = deliveredBy; }

    public List<Long> getReadBy() { return readBy; }
    public void setReadBy(List<Long> readBy) { this.readBy = readBy; }

    public Integer getTotalRecipients() { return totalRecipients; }
    public void setTotalRecipients(Integer totalRecipients) { this.totalRecipients = totalRecipients; }

    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public Boolean getEdited() { return edited; }
    public void setEdited(Boolean edited) { this.edited = edited; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
    
    
}
