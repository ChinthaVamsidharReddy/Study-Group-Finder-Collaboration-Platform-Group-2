package com.infy.project.Interface;


import com.infy.project.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
 List<ChatMessage> findByGroupIdOrderByTimestampAsc(Long groupId);
}
