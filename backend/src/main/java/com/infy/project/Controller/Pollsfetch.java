package com.infy.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.project.Dto.PollDTO;
import com.infy.project.Service.PollService;

@RestController
@RequestMapping("/polls")
public class Pollsfetch {
	
	@Autowired
	private PollService pollService;

	// ✅ REST API: Fetch all polls in a specific group
    @GetMapping("/group/{groupId}")
    public List<PollDTO> getPollsByGroupId(@PathVariable Long groupId) {
        // Convert Long to int if needed (Poll entity uses int for groupId)
        // Spring Data JPA will handle the conversion automatically
        return pollService.getPollsByGroupId(groupId);
    }

    // ✅ REST API: Get single poll (optional)
    @GetMapping("/{pollId}")
    public PollDTO getPollById(@PathVariable Long pollId) {
        return pollService.getPollById(pollId);
    }
}
