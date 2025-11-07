package com.infy.project.Controller;

import com.infy.project.Service.GroupService;
import com.infy.project.Dto.GroupDetailDto;
import com.infy.project.Dto.GroupJoinRequestDTO;
import com.infy.project.Dto.GroupRequestDTO;
import com.infy.project.Dto.GroupResponseDTO;
import com.infy.project.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    // 1️ Create Group
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(@RequestBody GroupRequestDTO groupRequest) {
//    	System.out.println("\n\n\n\n\n"+groupRequest.getCourseId()+"\n\n\n\n"+groupRequest.getCode());
        Long creatorId = groupRequest.getUserId();
        return ResponseEntity.ok(groupService.createGroup(groupRequest, creatorId));
    }

    // 2️ Get All Groups
    @GetMapping
    public ResponseEntity<List<GroupResponseDTO>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    // 3️ Join Group (Request or Direct)
    @PostMapping("/join/{groupId}")
    public ResponseEntity<String> joinGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(groupService.requestToJoin(groupId, userId));
    }

    // 4️ Approve Join Request (Admin only)
    @PostMapping("/approve/{memberId}")
    public ResponseEntity<String> approveMember(
            @PathVariable Long memberId,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(groupService.approveJoinRequest(memberId, adminId));
    }

    // 5️ Leave Group (Member)
    @DeleteMapping("/leave/{groupId}/{userId}")
    public ResponseEntity<String> leaveGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupService.leaveGroup(groupId, userId);
        return ResponseEntity.ok("You have left the group successfully.");
    }

    // 6️ Delete Group (Admin)
    @DeleteMapping("/delete/{groupId}/{adminId}")
    public ResponseEntity<String> deleteGroup(
            @PathVariable Long groupId,
            @PathVariable Long adminId) {
        groupService.deleteGroup(groupId, adminId);
        return ResponseEntity.ok("Group deleted successfully along with all its members.");
    }

    // 7️ Get Groups Created by a User
    @GetMapping("/created/{userId}")
    public ResponseEntity<List<GroupResponseDTO>> getCreatedGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(groupService.getGroupsCreatedByUserWithCount(userId));
    }

    // 8️ Get Groups Joined by a User
    @GetMapping("/joined/{userId}")
    public ResponseEntity<List<GroupResponseDTO>> getJoinedGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(groupService.getGroupsJoinedByUserWithCount(userId));
    }

    // 9️ Get Groups Available to Join (not created or joined)
    @GetMapping("/available/{userId}")
    public ResponseEntity<List<GroupResponseDTO>> getAvailableGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(groupService.getAvailableGroupsWithCount(userId));
    }
    
 // 10️ Get Pending Join Requests for a Group (Admin only)
    @GetMapping("/{groupId}/requests")
    public ResponseEntity<List<GroupJoinRequestDTO>> getPendingRequests(@PathVariable Long groupId) {
        List<GroupJoinRequestDTO> pendingRequests = groupService.getPendingJoinRequests(groupId);
        return ResponseEntity.ok(pendingRequests);
    }
    
    //	11 Rejection  Request (Admin only)
    @PostMapping("/reject/{memberId}")
    public ResponseEntity<String> rejectionMember(
            @PathVariable Long memberId,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(groupService.rejectRequest(memberId, adminId));
    }

    
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailDto> getGroupDetails(@PathVariable Long groupId) {
        GroupDetailDto groupDetail = groupService.getGroupDetailsById(groupId);
        return ResponseEntity.ok(groupDetail);
    }

}
