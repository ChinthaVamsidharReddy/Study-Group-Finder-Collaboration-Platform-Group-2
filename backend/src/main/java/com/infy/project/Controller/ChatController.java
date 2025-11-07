package com.infy.project.Controller;

import com.infy.project.Dto.*;
import com.infy.project.Service.ChatService;
import com.infy.project.payload.ChatPayload;
import com.infy.project.Dto.DeliveryAckDto;
import com.infy.project.Dto.ReadReceiptDto;
import com.infy.project.Dto.MessageStatusUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.infy.project.Dto.StatusPayload;
import com.infy.project.Dto.ChatMessageDto;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto message) {
        message.setTimestamp(LocalDateTime.now());
        ChatMessageDto saved = chatService.saveMessage(message);
        System.out.println("📩 Message from " + saved.getSenderName() + " in group " + saved.getGroupId());
        messagingTemplate.convertAndSend(
                "/topic/group." + saved.getGroupId(),
                new ChatPayload("message", saved.getGroupId(), saved)
        );
        // No immediate delivered; clients will ack delivered and we will broadcast status
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingNotificationDto typing) {
        System.out.println("✏️ Typing event from user " + typing.getUserName() + " in group " + typing.getGroupId());

        String eventType = typing.getType();
        messagingTemplate.convertAndSend(
            "/topic/group." + typing.getGroupId(),
            new ChatPayload(eventType, typing.getGroupId(), typing)
        );
    }

//    @MessageMapping("/chat.delivered")
//    public void delivered(@Payload DeliveryAckDto ack) {
//        chatService.markDelivered(ack.getMessageId(), ack.getUserId(), ack.getDeliveredAt());
//        MessageStatusUpdateDto dto = chatService.buildStatusSnapshot(ack.getGroupId(), ack.getMessageId());
//        messagingTemplate.convertAndSend(
//                "/topic/group." + ack.getGroupId(),
//                new ChatPayload("status", ack.getGroupId(), new StatusPayload("delivered", dto))
//        );
//        // also publish on chat.* topic for new consumers
//        messagingTemplate.convertAndSend(
//                "/topic/chat." + ack.getGroupId(),
//                new ChatPayload("status", ack.getGroupId(), new StatusPayload("delivered", dto))
//        );
//    }
    
    @MessageMapping("/chat.delivered")
    public void delivered(@Payload DeliveryAckDto ack) {
        MessageStatusUpdateDto dto = chatService.markDelivered(ack.getMessageId(), ack.getUserId(), ack.getDeliveredAt());
        messagingTemplate.convertAndSend("/topic/group." + ack.getGroupId(),
            new ChatPayload("status", ack.getGroupId(), new StatusPayload("delivered", dto)));
        messagingTemplate.convertAndSend("/topic/chat." + ack.getGroupId(),
            new ChatPayload("status", ack.getGroupId(), new StatusPayload("delivered", dto)));
    }


//    @MessageMapping("/chat.read")
//    public void read(@Payload ReadReceiptDto read) {
//        chatService.markRead(read.getMessageIds(), read.getUserId(), read.getReadAt());
//        for (Long messageId : read.getMessageIds()) {
//            MessageStatusUpdateDto dto = chatService.buildStatusSnapshot(read.getGroupId(), messageId);
//            messagingTemplate.convertAndSend(
//                    "/topic/group." + read.getGroupId(),
//                    new ChatPayload("status", read.getGroupId(), new StatusPayload("read", dto))
//            );
//            messagingTemplate.convertAndSend(
//                    "/topic/chat." + read.getGroupId(),
//                    new ChatPayload("status", read.getGroupId(), new StatusPayload("read", dto))
//            );
//        }
//    }
    
    @MessageMapping("/chat.read")
    public void read(@Payload ReadReceiptDto read) {
        List<MessageStatusUpdateDto> snapshots = chatService.markRead(read.getMessageIds(), read.getUserId(), read.getReadAt());
        for (MessageStatusUpdateDto dto : snapshots) {
            messagingTemplate.convertAndSend("/topic/group." + read.getGroupId(),
                new ChatPayload("status", read.getGroupId(), new StatusPayload("read", dto)));
            messagingTemplate.convertAndSend("/topic/chat." + read.getGroupId(),
                new ChatPayload("status", read.getGroupId(), new StatusPayload("read", dto)));
        }
    }

    @MessageMapping("/chat.reaction")
    public void reaction(@Payload ReactionEventDto reaction) {
        messagingTemplate.convertAndSend(
                "/topic/group." + reaction.getGroupId(),
                new ChatPayload("reaction", reaction.getGroupId(), reaction)
        );
    }

// @MessageMapping("/chat.pollVote")
// public void pollVote(@Payload PollVoteEventDto vote) {
//     messagingTemplate.convertAndSend(
//             "/topic/group." + vote.getGroupId(),
//             new ChatPayload("pollVote", vote.getGroupId(), vote)
//     );
// }
}
