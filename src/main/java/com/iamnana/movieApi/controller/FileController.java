package com.iamnana.movieApi.controller;

import com.iamnana.movieApi.service.file.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/file/")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    // File path from app settings
    @Value("${project.image}")
    private String path;

    @PostMapping("upload")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) throws IOException {
        String uploadedFile = fileService.uploadFile(path, file);
        return ResponseEntity.ok("uploaded file: " + uploadedFile);
    }

    @GetMapping("/{FileName}")
    public void serveFileOnWeb(@PathVariable String FileName, HttpServletResponse response) throws IOException {
        InputStream fileToSend = fileService.serveFileOnWeb(path, FileName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(fileToSend, response.getOutputStream());
    }
}
