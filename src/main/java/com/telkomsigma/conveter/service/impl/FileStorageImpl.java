package com.telkomsigma.conveter.service.impl;

import com.telkomsigma.conveter.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class FileStorageImpl implements FileStorageService {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final Path rootLocation = Paths.get("filestorage");

    private final Path rootLocationParam = Paths.get("fileparameter");
    @Override
    public void store(MultipartFile file,StringBuffer data,String fname){
        try {
            //String filename = FilenameUtils.removeExtension(file.getOriginalFilename()) + ".txt";
            BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(String.valueOf(this.rootLocation.resolve(fname+".txt")))));
            bwr.write(data.toString());
            bwr.flush();
            bwr.close();
            //Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    @Override
    public void storeParam(MultipartFile file) {
        try {
            File fil = convert(file);
            //Files.copy(fil, this.rootLocationParam.resolve(fil.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    @Override
    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error! -> message = " + e.getMessage());
        }
    }

    @Override
    public Resource loadFileParam(String filename) {
        try {
            Path file = rootLocationParam.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error! -> message = " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        Path file = rootLocation.resolve(filename);
        FileSystemUtils.deleteRecursively(file);
    }

    @Override
    public void deleteParam(String filename) throws IOException {
        Path file = rootLocationParam.resolve(filename);
        FileSystemUtils.deleteRecursively(file);
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
            Files.createDirectory(rootLocationParam);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }

    @Override
    public Stream loadFiles() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("\"Failed to read stored file");
        }
    }

    @Override
    public Stream loadParams() {
        try {
            return Files.walk(this.rootLocationParam, 1)
                    .filter(path -> !path.equals(this.rootLocationParam))
                    .map(this.rootLocationParam::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("\"Failed to read stored file");
        }
    }

    public File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        try(InputStream is = file.getInputStream()) {

            Files.copy(is, this.rootLocationParam.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        }
        return convFile;
    }
}