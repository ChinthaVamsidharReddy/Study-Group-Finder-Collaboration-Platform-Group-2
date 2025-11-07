package com.infy.project.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.project.Dto.AdminDto;
import com.infy.project.Dto.GroupDetailDto;
import com.infy.project.Dto.GroupJoinRequestDTO;
import com.infy.project.Dto.GroupRequestDTO;
import com.infy.project.Dto.GroupResponseDTO;
import com.infy.project.Dto.MemberDto;
import com.infy.project.Interface.GroupMemberRepository;
import com.infy.project.Interface.GroupRepository;
import com.infy.project.Interface.RegisterRepository;
import com.infy.project.model.Group;
import com.infy.project.model.GroupMember;
import com.infy.project.model.Register;
import com.infy.project.model.Role;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

    @Autowired
    private GroupRepository groupRepository; 

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private RegisterRepository registerRepository;

    /** 🔹 Create Group */
    public GroupResponseDTO createGroup(GroupRequestDTO request, Long creatorId) {
        if (groupRepository.existsByName(request.getName())) {
            throw new RuntimeException("Group name already exists!");
        }

        Group group = new Group();
        group.setName(request.getName());
        group.setCode(request.getCode());
        group.setDescription(request.getDescription());
        group.setCourseId(request.getCourseId());
        group.setCoursename(request.getCoursename());
        group.setPrivacy(request.getPrivacy());
        group.setCreatedBy(creatorId);
        group.setCreatedAt(LocalDateTime.now());

        Group savedGroup = groupRepository.save(group);

        // Add creator as admin
        GroupMember member = new GroupMember();
        member.setGroup(savedGroup);
        member.setUser(registerRepository.findById(creatorId).orElseThrow());
        member.setRole(Role.ADMIN);
        member.setJoinedAt(LocalDateTime.now());
        member.setApproved(true);
        groupMemberRepository.save(member);

        int memberCount = groupMemberRepository.countApprovedMembersByGroupId(savedGroup.getId());
        return new GroupResponseDTO(savedGroup, memberCount);
    }

    /** 🔹 Get all groups with member counts */
    public List<GroupResponseDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(group -> new GroupResponseDTO(group, groupMemberRepository.countApprovedMembersByGroupId(group.getId())))
                .collect(Collectors.toList());
    }

    /** 🔹 Request to join a group */
    public String requestToJoin(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                        .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean alreadyMember = groupMemberRepository.findByGroupId(groupId)
                .stream().anyMatch(m -> m.getUser().getId().equals(userId));
        if (alreadyMember) return "Already a member";

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(registerRepository.findById(userId).orElseThrow());
        member.setRole(Role.MEMBER);

        if ("PRIVATE".equalsIgnoreCase(group.getPrivacy())) {
            member.setApproved(false);
            groupMemberRepository.save(member);
            return "Request sent to admin for approval";
        } else {
            member.setApproved(true);
            member.setJoinedAt(LocalDateTime.now());
            groupMemberRepository.save(member);
            return "Joined group successfully";
        }
    }

    /** 🔹 Approve join request */
    public String approveJoinRequest(Long memberId, Long adminId) {
        GroupMember member = groupMemberRepository.findById(memberId)
                             .orElseThrow(() -> new RuntimeException("Member not found"));
        if (!member.getGroup().getCreatedBy().equals(adminId))
            throw new RuntimeException("Only admin can approve requests");

        member.setApproved(true);
        member.setJoinedAt(LocalDateTime.now());
        groupMemberRepository.save(member);
        return "User approved and added to group";
    }

    /** 🔹 Reject join request */
    @Transactional
    public String rejectRequest(Long memberId, Long adminId) {
        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found in this group"));

        if (!member.getGroup().getCreatedBy().equals(adminId)) {
            throw new RuntimeException("Only the group admin can reject requests");
        }

        groupMemberRepository.delete(member);
        return "Member rejected successfully.";
    }


    /** 🔹 Leave a group */
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in this group"));
        groupMemberRepository.delete(member);
    }

    /** 🔹 Delete a group */
    @Transactional
    public void deleteGroup(Long groupId, Long adminId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        System.out.println("\n\n\n\n\n\n\n\n\n\n*******"+adminId+"\n\n\n\n\n\n\n\n\n\n*******");
        GroupMember adminMember = groupMemberRepository.findByGroupIdAndUserId(groupId, adminId)
                .orElseThrow(() -> new RuntimeException("Admin not part of this group"));

        if(adminMember.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can delete the group");
        }

        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        groupMemberRepository.deleteAll(members);
        groupRepository.delete(group);
    }

    /** 🔹 Groups created by a user with member count */
    public List<GroupResponseDTO> getGroupsCreatedByUserWithCount(Long userId) {
        return groupRepository.findByCreatedBy(userId).stream()
                .map(group -> new GroupResponseDTO(group, groupMemberRepository.countApprovedMembersByGroupId(group.getId())))
                .collect(Collectors.toList());
    }

    /** 🔹 Groups joined by a user with member count */
    public List<GroupResponseDTO> getGroupsJoinedByUserWithCount(Long userId) {
        return groupMemberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroup)
                .distinct()
                .map(group -> new GroupResponseDTO(group, groupMemberRepository.countApprovedMembersByGroupId(group.getId())))
                .collect(Collectors.toList());
    }

    /** 🔹 Groups available to join with member count */
    public List<GroupResponseDTO> getAvailableGroupsWithCount(Long userId) {
        List<Group> allGroups = groupRepository.findAll();
        Set<Long> excludedIds = Stream.concat(
                getGroupsCreatedByUserWithCount(userId).stream().map(GroupResponseDTO::getId),
                getGroupsJoinedByUserWithCount(userId).stream().map(GroupResponseDTO::getId)
        ).collect(Collectors.toSet());

        return allGroups.stream()
                .filter(g -> !excludedIds.contains(g.getId()))
                .map(group -> new GroupResponseDTO(group, groupMemberRepository.countApprovedMembersByGroupId(group.getId())))
                .collect(Collectors.toList());
    }

    /** 🔹 Get pending join requests */
    public List<GroupJoinRequestDTO> getPendingJoinRequests(Long groupId) {
        List<GroupMember> pendingMembers = groupMemberRepository.findByGroupIdAndApprovedFalse(groupId);

        return pendingMembers.stream().map(member -> {
            Register user = member.getUser();
            return new GroupJoinRequestDTO(
                member.getId(),
                user.getId(),
                user.getName(),
                user.getMajor(),
                member.getJoinedAt() != null ? member.getJoinedAt().toString() : null
            );
        }).collect(Collectors.toList());
    }
    
    public GroupDetailDto getGroupDetailsById(Long groupId) {

        // 1️ Get group basic info
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // 2️ Get admin (createdBy user) details
        Register adminUser = registerRepository.findById(group.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        AdminDto admin = new AdminDto(adminUser.getId(), adminUser.getName(), adminUser.getMajor());

        // 3️ Get all members of the group
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

        List<MemberDto> memberDTOs = members.stream()
                .map((GroupMember m) -> {
                    Register user = m.getUser();
                    return new MemberDto(user.getId(), user.getName(), user.getMajor(), m.getRole().name());
                })
                .collect(Collectors.toList());

        // 4️ Create GroupDetailDTO to return
        return new GroupDetailDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCourseId(),
                admin,
                memberDTOs
        );
    }
}
