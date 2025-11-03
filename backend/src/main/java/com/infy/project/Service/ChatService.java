// com/infy/project/Service/ChatService.java
package com.infy.project.Service;

import com.infy.project.Dto.ChatMessageDto;
import com.infy.project.model.ChatMessage;
import com.infy.project.model.MessageStatus;
import com.infy.project.model.MessageStatusId;
import com.infy.project.Interface.ChatMessageRepository;
import com.infy.project.Interface.MessageRepository;
import com.infy.project.Interface.MessageFileRepository;
import com.infy.project.Interface.MessageStatusRepository;
import com.infy.project.Interface.GroupMemberRepository;
import com.infy.project.model.MessageFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageFileRepository messageFileRepository;
    @Autowired
    private MessageStatusRepository messageStatusRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    

//    @Transactional
//    public ChatMessageDto saveMessage(ChatMessageDto messageDto) {
//        ChatMessage message = new ChatMessage();
//        message.setGroupId(messageDto.getGroupId());
//        message.setSenderId(messageDto.getSenderId());
//        message.setSenderName(messageDto.getSenderName());
//        message.setContent(messageDto.getContent());
//        message.setTimestamp(messageDto.getTimestamp());
//        ChatMessage saved = chatMessageRepository.save(message);
//
//        ChatMessageDto savedDto = new ChatMessageDto(
//                saved.getId(),
//                saved.getGroupId(),
//                saved.getSenderId(),
//                saved.getSenderName(),
//                saved.getContent(),
//                saved.getTimestamp()
//        );
//        savedDto.setType(messageDto.getType());
//
//        // Initialize message_status rows for all approved members with null timestamps
//        try {
//            Long groupId = saved.getGroupId();
//            Long senderId = saved.getSenderId();
//            java.util.List<Long> memberIds = groupMemberRepository.findApprovedUserIdsByGroupId(groupId);
//            java.util.List<Long> deliveredTo = new java.util.ArrayList<>();
//            for (Long uid : memberIds) {
//                if (uid == null) continue;
//                MessageStatusId id = new MessageStatusId(saved.getId(), uid);
//                MessageStatus status = messageStatusRepository.findById(id).orElse(new MessageStatus(id));
//                // leave deliveredAt and readAt null; acks will set them idempotently
//                messageStatusRepository.save(status);
//                if (senderId == null || uid.longValue() != senderId.longValue()) {
//                    deliveredTo.add(uid);
//                }
//            }
//            savedDto.setDeliveredBy(deliveredTo);
//            savedDto.setReadBy(new java.util.ArrayList<>());
//            int total = Math.max(0, groupMemberRepository.countApprovedMembersByGroupId(groupId) - 1);
//            savedDto.setTotalRecipients(total);
//        } catch (Exception ignore) {}
//        return savedDto;
//    }
    
    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto messageDto) {
        ChatMessage message = new ChatMessage();
        message.setGroupId(messageDto.getGroupId());
        message.setSenderId(messageDto.getSenderId());
        message.setSenderName(messageDto.getSenderName());
        message.setContent(messageDto.getContent());
        message.setTimestamp(messageDto.getTimestamp());
        ChatMessage saved = chatMessageRepository.save(message);

        ChatMessageDto savedDto = new ChatMessageDto(
                saved.getId(),
                saved.getGroupId(),
                saved.getSenderId(),
                saved.getSenderName(),
                saved.getContent(),
                saved.getTimestamp()
        );
        savedDto.setType(messageDto.getType());

        // Initialize message_status rows for all approved members with null timestamps
        Long groupId = saved.getGroupId();
        Long senderId = saved.getSenderId();
        List<Long> memberIds = groupMemberRepository.findApprovedUserIdsByGroupId(groupId);
        // Insert rows (idempotent because repository.findById used)
        for (Long uid : memberIds) {
            if (uid == null) continue;
            MessageStatusId id = new MessageStatusId(saved.getId(), uid);
            MessageStatus status = messageStatusRepository.findById(id).orElse(new MessageStatus(id));
            // don't set deliveredAt/readAt here (leave null), unless you explicitly want sender marked
            // Optionally: if (uid.equals(senderId)) status.setDeliveredAt(saved.getTimestamp()); // if you want sender treated as delivered
            messageStatusRepository.save(status);
        }

        // Important: initially no one has delivered/read (except maybe sender if you set it above).
        savedDto.setDeliveredBy(new ArrayList<>());
        savedDto.setReadBy(new ArrayList<>());
        int total = Math.max(0, groupMemberRepository.countApprovedMembersByGroupId(groupId) - 1);
        savedDto.setTotalRecipients(total);

        return savedDto;
    }


    public List<ChatMessageDto> getMessagesByGroupId(Long groupId) {
        // Fetch from Message repository (includes type field for file messages)
        List<com.infy.project.model.Message> messages = messageRepository.findByGroupIdOrderByTimestampAsc(groupId);
        
        // Build a map of messageId -> MessageFile for efficient lookup
        Map<Long, MessageFile> fileMap = messageFileRepository.findAll().stream()
                .filter(mf -> mf.getMessage() != null && mf.getMessage().getGroupId().equals(groupId))
                .collect(Collectors.toMap(
                        mf -> mf.getMessage().getId(),
                        mf -> mf,
                        (existing, replacement) -> existing // Keep first if duplicates
                ));
        
        return messages.stream()
                .map(msg -> {
                    ChatMessageDto dto = new ChatMessageDto(
                            msg.getId(),
                            msg.getGroupId(),
                            msg.getSenderId(),
                            msg.getSenderName(),
                            msg.getContent(),
                            msg.getTimestamp()
                    );
                    dto.setType(msg.getType() != null ? msg.getType() : "text"); // Set type (text, file, etc.)
                    
                    // If it's a file message, fetch file metadata from map
                    if ("file".equals(msg.getType())) {
                        MessageFile mf = fileMap.get(msg.getId());
                        if (mf != null) {
                            dto.setFileUrl(mf.getFileUrl());
                            dto.setFileType(mf.getFileType());
                            dto.setFileSize(mf.getSize());
                            // Update content to fileName for consistency
                            if (mf.getFileName() != null && !mf.getFileName().isEmpty()) {
                                dto.setContent(mf.getFileName());
                            }
                        }
                    }
                    return dto;
                })
                .peek(dto -> {
                    // hydrate delivered/read lists from message_status
                    var statuses = messageStatusRepository.findByIdMessageId(dto.getId());
                    java.util.List<Long> delivered = new java.util.ArrayList<>();
                    java.util.List<Long> read = new java.util.ArrayList<>();
                    for (var s : statuses) {
                        if (s.getDeliveredAt() != null) delivered.add(s.getId().getUserId());
                        if (s.getReadAt() != null) read.add(s.getId().getUserId());
                    }
                    dto.setDeliveredBy(delivered);
                    dto.setReadBy(read);
                    // total recipients = approved members - 1 (exclude sender)
                    int total = Math.max(0, groupMemberRepository.countApprovedMembersByGroupId(dto.getGroupId()) - 1);
                    dto.setTotalRecipients(total);
                })
                .collect(Collectors.toList());
    }

    public java.util.List<Long> getDeliveredUserIds(Long messageId) {
        var statuses = messageStatusRepository.findByIdMessageId(messageId);
        java.util.List<Long> delivered = new java.util.ArrayList<>();
        for (var s : statuses) if (s.getDeliveredAt() != null) delivered.add(s.getId().getUserId());
        return delivered;
    }

    public java.util.List<Long> getReadUserIds(Long messageId) {
        var statuses = messageStatusRepository.findByIdMessageId(messageId);
        java.util.List<Long> read = new java.util.ArrayList<>();
        for (var s : statuses) if (s.getReadAt() != null) read.add(s.getId().getUserId());
        return read;
    }

    public int getTotalRecipients(Long groupId) {
        return Math.max(0, groupMemberRepository.countApprovedMembersByGroupId(groupId) - 1);
    }

//    public com.infy.project.Dto.MessageStatusUpdateDto buildStatusSnapshot(Long groupId, Long messageId) {
//        var dto = new com.infy.project.Dto.MessageStatusUpdateDto();
//        dto.setGroupId(groupId);
//        dto.setMessageId(messageId);
//        Long senderId = null;
//        try {
//            senderId = chatMessageRepository.findById(messageId).map(com.infy.project.model.ChatMessage::getSenderId).orElse(null);
//        } catch (Exception ignore) {}
//        var deliveredAll = getDeliveredUserIds(messageId);
//        var readAll = getReadUserIds(messageId);
//        if (senderId != null) {
//            deliveredAll.removeIf(uid -> uid != null && uid.longValue() == senderId.longValue());
//            readAll.removeIf(uid -> uid != null && uid.longValue() == senderId.longValue());
//        }
//        dto.setDeliveredBy(deliveredAll);
//        dto.setReadBy(readAll);
//        dto.setTotalRecipients(getTotalRecipients(groupId));
//        return dto;
//    }
    
    public com.infy.project.Dto.MessageStatusUpdateDto buildStatusSnapshot(Long groupId, Long messageId) {
        var dto = new com.infy.project.Dto.MessageStatusUpdateDto();
        dto.setGroupId(groupId);
        dto.setMessageId(messageId);

        Long senderId = chatMessageRepository.findById(messageId)
                .map(com.infy.project.model.ChatMessage::getSenderId)
                .orElse(null);

        var statuses = messageStatusRepository.findByIdMessageId(messageId);
        List<Long> deliveredAll = new ArrayList<>();
        List<Long> readAll = new ArrayList<>();
        Map<Long, java.time.LocalDateTime> deliveredMap = new java.util.HashMap<>();
        Map<Long, java.time.LocalDateTime> readMap = new java.util.HashMap<>();

        for (MessageStatus s : statuses) {
            Long uid = s.getId().getUserId();
            if (s.getDeliveredAt() != null) {
                deliveredAll.add(uid);
                deliveredMap.put(uid, s.getDeliveredAt());
            }
            if (s.getReadAt() != null) {
                readAll.add(uid);
                readMap.put(uid, s.getReadAt());
            }
        }

        // Exclude sender from delivered/read lists used for aggregation (so sender doesn't count)
        if (senderId != null) {
            deliveredAll.removeIf(uid -> senderId.equals(uid));
            readAll.removeIf(uid -> senderId.equals(uid));
            deliveredMap.remove(senderId);
            readMap.remove(senderId);
        }

        dto.setDeliveredBy(deliveredAll);
        dto.setReadBy(readAll);
        dto.setDeliveredAtMap(deliveredMap); // add maps to DTO so front-end can show times if needed
        dto.setReadAtMap(readMap);
        dto.setTotalRecipients(getTotalRecipients(groupId));
        return dto;
    }


//    @Transactional
//    public void markDelivered(Long messageId, Long userId, java.time.LocalDateTime deliveredAt) {
//        MessageStatusId id = new MessageStatusId(messageId, userId);
//        MessageStatus status = messageStatusRepository.findById(id).orElse(new MessageStatus(id));
//        if (status.getDeliveredAt() == null) {
//            status.setDeliveredAt(deliveredAt != null ? deliveredAt : java.time.LocalDateTime.now());
//            messageStatusRepository.save(status);
//        }
//    }
    
    @Transactional
    public com.infy.project.Dto.MessageStatusUpdateDto markDelivered(Long messageId, Long userId, java.time.LocalDateTime deliveredAt) {
        MessageStatusId id = new MessageStatusId(messageId, userId);
        MessageStatus status = messageStatusRepository.findById(id).orElse(new MessageStatus(id));
        if (status.getDeliveredAt() == null) {
            status.setDeliveredAt(deliveredAt != null ? deliveredAt : java.time.LocalDateTime.now());
            messageStatusRepository.save(status);
        }
        // return current snapshot for broadcasting
        Long groupId = chatMessageRepository.findById(messageId).map(ChatMessage::getGroupId).orElse(null);
        return buildStatusSnapshot(groupId, messageId);
    }


//    @Transactional
//    public void markRead(List<Long> messageIds, Long userId, java.time.LocalDateTime readAt) {
//        java.time.LocalDateTime ts = readAt != null ? readAt : java.time.LocalDateTime.now();
//        for (Long messageId : messageIds) {
//            MessageStatusId id = new MessageStatusId(messageId, userId);
//            MessageStatus status = messageStatusRepository.findById(id).orElse(new MessageStatus(id));
//            boolean changed = false;
//            if (status.getReadAt() == null) { status.setReadAt(ts); changed = true; }
//            if (status.getDeliveredAt() == null) { status.setDeliveredAt(ts); changed = true; }
//            if (changed) {
//                messageStatusRepository.save(status);
//            }
//        }
//    }
    
    @Transactional
    public List<com.infy.project.Dto.MessageStatusUpdateDto> markRead(List<Long> messageIds, Long userId, java.time.LocalDateTime readAt) {
        java.time.LocalDateTime ts = readAt != null ? readAt : java.time.LocalDateTime.now();
        List<com.infy.project.Dto.MessageStatusUpdateDto> snapshots = new ArrayList<>();
        for (Long messageId : messageIds) {
            MessageStatusId id = new MessageStatusId(messageId, userId);
            MessageStatus status = messageStatusRepository.findById(id).orElse(new MessageStatus(id));
            boolean changed = false;
            if (status.getReadAt() == null) { status.setReadAt(ts); changed = true; }
            // Option A: treat read as implicit delivered if delivered missing (ok but will make deliver==read)
            if (status.getDeliveredAt() == null) { 
                // If you want to distinguish delivered & read timestamps, you must rely on client to send delivered ack;
                // here we set deliveredAt to read time only to avoid nulls when delivered ack was lost.
                status.setDeliveredAt(ts); 
                changed = true; 
            }
            if (changed) messageStatusRepository.save(status);

            Long groupId = chatMessageRepository.findById(messageId).map(ChatMessage::getGroupId).orElse(null);
            snapshots.add(buildStatusSnapshot(groupId, messageId));
        }
        return snapshots;
    }
}
