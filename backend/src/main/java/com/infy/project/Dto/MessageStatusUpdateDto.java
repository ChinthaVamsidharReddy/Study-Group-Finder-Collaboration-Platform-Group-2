package com.infy.project.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MessageStatusUpdateDto {
    private Long groupId;
    private Long messageId;
    private List<Long> deliveredBy;
    private List<Long> readBy;
    private Integer totalRecipients;

    // optional maps so frontend can see per-user timestamps
    private Map<Long, LocalDateTime> deliveredAtMap;
    private Map<Long, LocalDateTime> readAtMap;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public List<Long> getDeliveredBy() { return deliveredBy; }
    public void setDeliveredBy(List<Long> deliveredBy) { this.deliveredBy = deliveredBy; }

    public List<Long> getReadBy() { return readBy; }
    public void setReadBy(List<Long> readBy) { this.readBy = readBy; }

    public Integer getTotalRecipients() { return totalRecipients; }
    public void setTotalRecipients(Integer totalRecipients) { this.totalRecipients = totalRecipients; }

    public Map<Long, LocalDateTime> getDeliveredAtMap() { return deliveredAtMap; }
    public void setDeliveredAtMap(Map<Long, LocalDateTime> deliveredAtMap) { this.deliveredAtMap = deliveredAtMap; }

    public Map<Long, LocalDateTime> getReadAtMap() { return readAtMap; }
    public void setReadAtMap(Map<Long, LocalDateTime> readAtMap) { this.readAtMap = readAtMap; }
}
