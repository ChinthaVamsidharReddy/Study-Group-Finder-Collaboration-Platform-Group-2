package com.infy.project.Dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageFileDTO {
    private Long messageId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long size;
    private Long senderId;
    private String senderName;
    private LocalDateTime timestamp;
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
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
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
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
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public MessageFileDTO(Long messageId, String fileName, String fileUrl, String fileType, Long size, Long senderId,
			String senderName, LocalDateTime timestamp) {
		super();
		this.messageId = messageId;
		this.fileName = fileName;
		this.fileUrl = fileUrl;
		this.fileType = fileType;
		this.size = size;
		this.senderId = senderId;
		this.senderName = senderName;
		this.timestamp = timestamp;
	}
	public MessageFileDTO() {
		super();
	}
    
    
    
}

