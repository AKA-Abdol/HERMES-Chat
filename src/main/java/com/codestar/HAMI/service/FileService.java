package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.File;
import com.codestar.HAMI.entity.FileTypeEnum;
import com.codestar.HAMI.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FileService {

    @Autowired
    FileRepository fileRepository;
    public File saveFile(String fileContentType, byte[] data) {
        File file = new File();
        file.setData(data);
        file.setContentType(fileContentType);
        return fileRepository.save(file);
    }

//    private FileTypeEnum recognizeFileType(String fileContentType) {
//        FileTypeEnum type;
//        String[] temp = fileContentType.split("/", 2);
//        try {
//            type = FileTypeEnum.valueOf(temp[0].toUpperCase());
//        }
//        catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type.");
//        }
//        return type;
//    }

    public void removeFile(Long fileId) {
        File file = fileRepository.findById(fileId).orElse(null);
        if(file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this file id doesn't exist!");
        }
        fileRepository.delete(file);
    }

    public File getFileById(Long fileId) {
        File file = fileRepository.findById(fileId).orElse(null);
        if(file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this file id doesn't exist!");
        }
        return file;
    }

    public List<File> getFiles() {
        return fileRepository.findAll();
    }

    public ResponseEntity<byte[]> downloadFileByFileId(File file) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.valueOf(file.getContentType()));
        ContentDisposition build = ContentDisposition
                .builder("inline")
                .filename("Hossin")
                .build();
        header.setContentDisposition(build);
        return new ResponseEntity<>(file.getData(), header, HttpStatus.OK);
    }

}
