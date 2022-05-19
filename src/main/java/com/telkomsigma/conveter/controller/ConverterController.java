package com.telkomsigma.conveter.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telkomsigma.conveter.errorHandler.ExceptionConvertHandler;
import com.telkomsigma.conveter.service.ConverterService;
import com.telkomsigma.conveter.service.FileStorageService;


@Controller
public class ConverterController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

	
	@Autowired FileStorageService fileStorage;
	 
	@Autowired ConverterService converter;
	 
    //private static final String UPLOAD_DIR = "D:/";

    @RequestMapping(value = "/")
    public String home() {
        return "home";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("uploadfile") MultipartFile file,@RequestParam("filename") String fname,RedirectAttributes attributes) throws Exception {

    	if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }else {
        	converter.convert(file, fname,attributes);
        }
        return "redirect:/";
    }

    @ExceptionHandler({ExceptionConvertHandler.class})
    public String getErrorException(ExceptionConvertHandler ex,RedirectAttributes attributes) {
        attributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/";
    }

    @GetMapping("/about")
    public String getListFiles() {
        return "about";
    }
}
