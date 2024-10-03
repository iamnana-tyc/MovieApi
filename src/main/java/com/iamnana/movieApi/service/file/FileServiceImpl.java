package com.iamnana.movieApi.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // first get the file name
        String getFileName = file.getOriginalFilename();

        // we get the path
        String filePath = path + File.separator + getFileName;

        //we need to create file object
        File myFile = new File(path);
        if(!myFile.exists()){
            myFile.mkdir();
        }
        // upload or copy the file
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        return getFileName;
    }

    @Override
    public InputStream serveFileOnWeb(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;
        return new FileInputStream(filePath);
    }
}
