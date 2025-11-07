package com.infy.project.Service;

import com.infy.project.Dto.*;
import com.infy.project.Interface.PollRepository;
import com.infy.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    /* ----------------- Create Poll ----------------- */
    @Transactional
    public Poll createPoll(PollDTO pollDTO) {
        if (pollDTO == null) {
            throw new IllegalArgumentException("Poll data cannot be null");
        }

        Poll poll = new Poll();
        poll.setQuestion(pollDTO.getQuestion());
        poll.setAllowMultiple(pollDTO.isAllowMultiple());
        poll.setAnonymous(pollDTO.isAnonymous());
        poll.setGroupId(pollDTO.getGroupId());
        poll.setCreatedAt(Instant.now());

        
        poll.setCreatedBy(pollDTO.getCreatorId());
        poll.setCreatorName(pollDTO.getCreatorName());
        List<PollOption> options = Optional.ofNullable(pollDTO.getOptions())
                .orElse(Collections.emptyList()) // ✅ avoids null
                .stream()
                .map(opt -> {
                    PollOption o = new PollOption();
                    o.setText(opt.getText());
                    o.setPoll(poll);
                    return o;
                })
                .collect(Collectors.toList());

        poll.setOptions(options);
        poll.setTotalVotes(0);
        
        System.out.println("\n\n\n\n\n\n\n\n inside the service class \n\n\n\n\n"+pollDTO.getCreatedAt());
        return pollRepository.save(poll);
    }

    /* ----------------- Get Polls ----------------- */
    public List<Poll> getPollsByGroup(Long groupId) {
        return pollRepository.findByGroupId(groupId);
    }

//    public Optional<Poll> getPollById(Long id) {
//        return pollRepository.findById(id);
//    }
    
    
    //    ----------Vote Poll---------------
    @Transactional
    public Poll votePoll(PollVoteRequest voteReq) {
        Poll poll = pollRepository.findById(voteReq.getPollId())
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        // Check if this specific user has already voted
        boolean userAlreadyVoted = poll.getOptions().stream()
                .flatMap(opt -> opt.getVotes().stream())
                .anyMatch(v -> v.getUserId() != null && v.getUserId().equals(voteReq.getUserId()));

        // If multiple votes are not allowed and this user has already voted
        if (userAlreadyVoted && !poll.isAllowMultiple()) {
            throw new RuntimeException("You have already voted in this poll.");
        }

        // Record new votes
        for (PollOption option : poll.getOptions()) {
            if (voteReq.getOptionIds().contains(option.getId())) {
                // Check if user already voted for this specific option
                boolean hasVotedThisOption = option.getVotes().stream()
                        .anyMatch(v -> v.getUserId() != null && v.getUserId().equals(voteReq.getUserId()));
                if (!hasVotedThisOption) {
                    PollVote vote = new PollVote();
                    vote.setUserId(voteReq.getUserId());
                    vote.setOption(option);
                    vote.setPoll(poll);
                    option.getVotes().add(vote);
                }
            }
        }

        // Recalculate total votes after voting
        poll.setTotalVotes(
            poll.getOptions().stream().mapToInt(opt -> opt.getVotes().size()).sum()
        );

        return pollRepository.save(poll);
    }

    /* ----------------- DTO Converter ----------------- */
    public PollDTO convertToDTO(Poll poll) {
        PollDTO dto = new PollDTO();
        dto.setId(poll.getId());
        dto.setGroupId(poll.getGroupId());
        dto.setQuestion(poll.getQuestion());
        dto.setAllowMultiple(poll.isAllowMultiple());
        dto.setCreatedAt(poll.getCreatedAt());
        dto.setAnonymous(poll.isAnonymous());
        dto.setCreatorId(poll.getCreatedBy());
        dto.setCreatorName(poll.getCreatorName());

        List<PollOptionDTO> optionDTOs = poll.getOptions().stream().map(opt -> {
            PollOptionDTO o = new PollOptionDTO();
            o.setId(opt.getId());
            o.setText(opt.getText());
            o.setVotes(opt.getVotes().stream()
                    .map(PollVote::getUserId)
                    .collect(Collectors.toList()));
            return o;
        }).collect(Collectors.toList());

        dto.setOptions(optionDTOs);
        dto.setTotalVotes(
                poll.getOptions().stream().mapToInt(opt -> opt.getVotes().size()).sum()
        );
        return dto;
    }

    
    public List<PollDTO> getPollsByGroupId(Long groupId) {
        // Convert Long to int for query (Poll entity uses int)
        // Spring Data JPA should handle this, but ensure conversion
        int groupIdInt = groupId != null ? groupId.intValue() : 0;
        return pollRepository.findByGroupId(groupId)
                .stream()
                .filter(poll -> poll.getGroupId() == groupIdInt) // Extra safety check
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PollDTO getPollById(Long pollId) {
        return pollRepository.findById(pollId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Poll not found"));
    }
}
