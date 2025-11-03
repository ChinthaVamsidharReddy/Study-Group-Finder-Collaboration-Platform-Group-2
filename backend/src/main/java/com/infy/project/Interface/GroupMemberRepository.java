package com.infy.project.Interface;

import com.infy.project.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	
    List<GroupMember> findByUserId(Long userId);
    List<GroupMember> findByGroupId(Long groupId);
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMember> findByGroupIdAndApprovedFalse(Long groupId);

    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.approved = true")
    int countApprovedMembersByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT gm.user.id FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.approved = true")
    List<Long> findApprovedUserIdsByGroupId(@Param("groupId") Long groupId);


}
