package com.telkomsigma.conveter.controller;

import com.telkomsigma.conveter.model.FileInfo;
import com.telkomsigma.conveter.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class DownloadController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    FileStorageService fileStorage;

    /*
     * Retrieve Files' Information
     */
    @GetMapping("/files")
    public String getListFiles(Model model) {
        List fileInfos = fileStorage.loadFiles().map(
                path ->	{
                    String filename = path.getFileName().toString();
                    String url = MvcUriComponentsBuilder.fromMethodName(DownloadController.class,
                            "downloadFile", path.getFileName().toString()).build().toString();
                    return new FileInfo(filename, url);
                }
        )
                .collect(Collectors.toList());

        List fileParams = fileStorage.loadParams().map(
                path1 ->	{
                    String filename1 = path1.getFileName().toString();
                    String url = MvcUriComponentsBuilder.fromMethodName(DownloadController.class,
                            "downloadFileParam", path1.getFileName().toString()).build().toString();
                    return new FileInfo(filename1, url);
                }
        )
                .collect(Collectors.toList());
        model.addAttribute("files", fileInfos);
        model.addAttribute("fileparams", fileParams);
        return "listfiles";
    }

    /*
     * Download Files
     */
    @GetMapping("/files/file/{filename}")
    public ResponseEntity downloadFile(@PathVariable String filename) {
        Resource file = fileStorage.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/files/param/{filename}")
    public ResponseEntity downloadFileParam(@PathVariable String filename) {
        Resource file = fileStorage.loadFileParam(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping(value = "delete_file")
    public String handleDeleteFile(@RequestParam(name="filename")String filename) throws IOException {
        fileStorage.deleteFile(filename);
        return "redirect:/files";
    }

    @GetMapping(value = "delete_param")
    public String handleDeleteParam(@RequestParam(name="filename")String filename) throws IOException {
        fileStorage.deleteParam(filename);
        return "redirect:/files";
    }
}
