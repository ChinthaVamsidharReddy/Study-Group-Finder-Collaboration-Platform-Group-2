package com.infy.project.Interface;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.project.model.MessageFile;

public interface MessageFileRepository extends JpaRepository<MessageFile, Long> {}
