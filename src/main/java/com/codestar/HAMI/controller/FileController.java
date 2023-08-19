package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.File;
import com.codestar.HAMI.entity.FileTypeEnum;
import com.codestar.HAMI.model.FileModel;
import com.codestar.HAMI.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping(consumes = {
            "multipart/form-data"})
    public FileModel uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        File upFile = new File();
        upFile = fileService.saveFile(file.getContentType(), file.getBytes());
        return FileModel.builder()
                .id(upFile.getId())
                .data(upFile.getData())
                .ContentType(upFile.getContentType())
                .build();
    }

    @DeleteMapping("{fileId}")
    public void deleteFile(@PathVariable Long fileId) {
        fileService.removeFile(fileId);
    }

    @GetMapping("{fileId}")
    public FileModel getFile(@PathVariable Long fileId) {
        File file = fileService.getFileById(fileId);
        return FileModel.builder()
                .id(file.getId())
                .ContentType(file.getContentType())
                .data(file.getData())
                .build();
    }

    @GetMapping()
    public List<FileModel> getAllFiles() {
        List<File> files = fileService.getFiles();
        List<FileModel> fileModels = new ArrayList<>();
        for(File file: files) {
            fileModels.add(FileModel.builder()
                    .id(file.getId())
                    .ContentType(file.getContentType())
                    .data(file.getData())
                    .build());
        }
        return fileModels;
    }

    @GetMapping(path = "/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        File downloadFile = fileService.getFileById(fileId);
        if(downloadFile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this file id doesn't exist!");
        }
        return fileService.downloadFileByFileId(downloadFile);
    }

}
