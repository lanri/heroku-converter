package com.telkomsigma.conveter.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    public void store(MultipartFile file,StringBuffer data,String fname);
    public void storeParam(MultipartFile file);
    public Resource loadFile(String filename);
    public Resource loadFileParam(String filename);
    public void deleteAll();
    public void deleteFile(String filename) throws IOException;
    public void deleteParam(String filename) throws IOException;
    public void init();
    public Stream<Path> loadFiles();
    public Stream<Path> loadParams();


}
