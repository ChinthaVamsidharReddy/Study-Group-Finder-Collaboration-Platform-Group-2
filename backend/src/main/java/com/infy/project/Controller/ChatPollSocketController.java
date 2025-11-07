package com.infy.project.Controller;

import com.infy.project.Dto.PollDTO;
import com.infy.project.Dto.PollVoteRequest;
import com.infy.project.Service.PollService;
import com.infy.project.model.Poll;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatPollSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PollService pollService;

//    @MessageMapping("/chat.sendPoll")
//    public void handlePoll(@Payload PollDTO pollDTO) {
//    	System.out.println(pollDTO.toString());
//        var saved = pollService.createPoll(pollDTO);
//        var dto = pollService.convertToDTO(saved);
//        messagingTemplate.convertAndSend(
//                "/topic/group." + pollDTO.getGroupId(),
//                new SocketPayload("poll", pollDTO.getGroupId(), dto)
//        );
//    }
    
 // Inside your WebSocket handler (like PollController or ChatController)
 // import at top of file


    // inside ChatPollSocketController
    @MessageMapping("/chat.sendPoll")
    public void handlePoll(@Payload PollDTO pollDTO) {
        // save
        Poll savedPoll = pollService.createPoll(pollDTO);
        PollDTO dto = pollService.convertToDTO(savedPoll);

        // ensure createdAt present and in Instant
        if (dto.getCreatedAt() == null) {
            dto.setCreatedAt(savedPoll.getCreatedAt() != null ? savedPoll.getCreatedAt() : Instant.now());
        }

        // ensure creator info present on DTO (adjust method names to your Poll entity)
        // If your Poll entity has fields like getCreatedBy() and getCreatorName() use those,
        // otherwise adjust accordingly.
        try {
            if (dto.getCreatorId() == null && savedPoll.getCreatedBy() != null) {
                dto.setCreatorId(savedPoll.getCreatedBy());
            }
            if ((dto.getCreatorName() == null || dto.getCreatorName().isEmpty()) && savedPoll.getCreatorName() != null) {
                dto.setCreatorName(savedPoll.getCreatorName());
            }
        } catch (NoSuchMethodError | NoSuchFieldError ex) {
            // ignore if your Poll entity doesn't have these exact fields;
            // best to adapt to real field names on Poll model.
        }

        // Convert groupId to string if your SocketPayload expects a String; otherwise pass int
        messagingTemplate.convertAndSend("/topic/group." + savedPoll.getGroupId(),
                new SocketPayload("poll", (int)savedPoll.getGroupId(), dto));
    }



    @MessageMapping("/chat.pollVote")
    public void handlePollVote(@Payload PollVoteRequest voteReq) {
    	
    	System.out.println("step -1 ");
    	
        var updated = pollService.votePoll(voteReq);
        var dto = pollService.convertToDTO(updated);
        
        System.out.println("step -1 ");

        // Broadcast the updated poll to everyone in the group
        messagingTemplate.convertAndSend(
            "/topic/group." + voteReq.getGroupId(),
            new SocketPayload("poll_vote", voteReq.getGroupId(), dto)
        );
    }

    
 // ✅ REST API: Fetch all polls in a specific group
    @GetMapping("/group/{groupId}")
    public List<PollDTO> getPollsByGroupId(@PathVariable Long groupId) {
        return pollService.getPollsByGroupId(groupId);
    }

    // ✅ REST API: Get single poll (optional)
    @GetMapping("/{pollId}")
    public PollDTO getPollById(@PathVariable Long pollId) {
        return pollService.getPollById(pollId);
    }
}

