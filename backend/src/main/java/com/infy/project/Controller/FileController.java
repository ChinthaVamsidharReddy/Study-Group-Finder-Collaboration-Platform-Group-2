package com.infy.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.project.Dto.MessageFileDTO;
import com.infy.project.Service.FileService;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files")
public class FileController {

	@Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<MessageFileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("groupId") Long groupId,
            @RequestParam("senderId") Long senderId,
            @RequestParam("senderName") String senderName
    ) {
    	System.out.println(senderName+" \n\n\n"+senderId+"\n\n\n");
        MessageFileDTO uploaded = fileService.saveFile(file, groupId, senderId, senderName);
        return ResponseEntity.ok(uploaded);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        Resource resource = fileService.loadAsResource(filename);
        
        // Extract original filename from stored filename (remove timestamp prefix)
        String originalFilename = filename;
        int underscoreIndex = filename.indexOf('_');
        if (underscoreIndex > 0) {
            originalFilename = filename.substring(underscoreIndex + 1);
        }
        
        // Determine content type based on file extension
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        String lowerFilename = originalFilename.toLowerCase();
        if (lowerFilename.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (lowerFilename.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (lowerFilename.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else if (lowerFilename.endsWith(".txt")) {
            mediaType = MediaType.TEXT_PLAIN;
        } else if (lowerFilename.endsWith(".doc") || lowerFilename.endsWith(".docx")) {
            mediaType = MediaType.parseMediaType("application/msword");
        } else if (lowerFilename.endsWith(".xls") || lowerFilename.endsWith(".xlsx")) {
            mediaType = MediaType.parseMediaType("application/vnd.ms-excel");
        } else if (lowerFilename.endsWith(".zip")) {
            mediaType = MediaType.parseMediaType("application/zip");
        }
        
        // Set Content-Disposition header for download
        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(originalFilename, StandardCharsets.UTF_8)
                .build();
        
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
}

