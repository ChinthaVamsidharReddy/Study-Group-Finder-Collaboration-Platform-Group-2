package com.infy.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_status")
public class MessageStatus {

    @EmbeddedId
    private MessageStatusId id;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public MessageStatus() {}

    public MessageStatus(MessageStatusId id) {
        this.id = id;
    }

    public MessageStatusId getId() { return id; }
    public void setId(MessageStatusId id) { this.id = id; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}

