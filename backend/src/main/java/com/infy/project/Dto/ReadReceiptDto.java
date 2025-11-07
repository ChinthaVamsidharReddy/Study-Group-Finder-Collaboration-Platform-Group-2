package com.infy.project.Dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReadReceiptDto {
    private Long groupId;
    private List<Long> messageIds;
    private Long userId;
    private LocalDateTime readAt;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public List<Long> getMessageIds() { return messageIds; }
    public void setMessageIds(List<Long> messageIds) { this.messageIds = messageIds; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}


