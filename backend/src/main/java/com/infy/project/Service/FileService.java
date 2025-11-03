package com.infy.project.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.project.Controller.SocketPayload;
import com.infy.project.Dto.MessageFileDTO;
import com.infy.project.Interface.MessageFileRepository;
import com.infy.project.Interface.MessageRepository;
import com.infy.project.model.Message;
import com.infy.project.model.MessageFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
public class FileService {

    private final Path root = Paths.get("uploads");
    
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private  MessageFileRepository fileRepository;
    @Autowired
    private  SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageFileDTO saveFile(MultipartFile file, Long groupId, Long senderId, String senderName) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = root.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ✅ Create Message entry
            Message msg = new Message();
            msg.setGroupId(groupId);
            msg.setSenderId(senderId);
            msg.setSenderName(senderName);
            msg.setType("file");
            msg.setContent(file.getOriginalFilename());
            msg.setTimestamp(LocalDateTime.now());
            messageRepository.save(msg);

            // ✅ Save metadata
            MessageFile mf = new MessageFile();
            mf.setFileName(file.getOriginalFilename());
            mf.setFileType(file.getContentType());
            mf.setFileUrl("/api/files/" + fileName);
            mf.setSize(file.getSize());
            mf.setMessage(msg);
            fileRepository.save(mf);

            // ✅ WebSocket broadcast
            MessageFileDTO dto = new MessageFileDTO(
            	    msg.getId(), 
            	    mf.getFileName(), 
            	    mf.getFileUrl(), 
            	    mf.getFileType(), 
            	    mf.getSize(), 
            	    msg.getSenderId(), 
            	    msg.getSenderName(), 
            	    msg.getTimestamp()
            	);

            	messagingTemplate.convertAndSend(
            	    "/topic/group." + groupId,
            	    new SocketPayload("file", Math.toIntExact(groupId), dto)  // ✅ Remove object payload from "content"
            	);


            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    public Resource loadAsResource(String filename) throws IOException {
        Path file = root.resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found: " + filename);
        }
    }
}

