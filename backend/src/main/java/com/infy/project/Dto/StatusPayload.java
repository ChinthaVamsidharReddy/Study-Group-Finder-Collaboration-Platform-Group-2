package com.infy.project.Dto;

public class StatusPayload {
    private String statusType; // "delivered" | "read"
    private MessageStatusUpdateDto status;

    public StatusPayload() {}

    public StatusPayload(String statusType, MessageStatusUpdateDto status) {
        this.statusType = statusType;
        this.status = status;
    }

    public String getStatusType() { return statusType; }
    public void setStatusType(String statusType) { this.statusType = statusType; }

    public MessageStatusUpdateDto getStatus() { return status; }
    public void setStatus(MessageStatusUpdateDto status) { this.status = status; }
}
