package com.infy.project.Interface;

import com.infy.project.model.MessageStatus;
import com.infy.project.model.MessageStatusId;
import com.infy.project.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, MessageStatusId> {
    List<MessageStatus> findByIdMessageId(Long messageId);
}


