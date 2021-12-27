package com.telkomsigma.conveter.controller;

import com.telkomsigma.conveter.model.ExceptionConvertHandler;
import com.telkomsigma.conveter.service.Converter;
import com.telkomsigma.conveter.service.FileStorage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
public class ConverterController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    FileStorage fileStorage;

    @Autowired
    Converter converter;

    private static final String UPLOAD_DIR = "D:/";

    @RequestMapping(value = "/")
    public String home() {
        return "home";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("uploadfile") MultipartFile file, @RequestParam("fileparam") MultipartFile param,@RequestParam("filename") String fname,RedirectAttributes attributes) throws Exception {

        if (file.isEmpty()||param.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            //validate file Xlsx or Xls
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {

                //convert to text file
                StringBuffer data = converter.convert(file,param,attributes);
                //store to filestorage
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String strDate = dateFormat.format(timestamp);
                fileStorage.store(file, data,fname+strDate);
                fileStorage.storeParam(param);
                // return success response
                attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

            } else {
                attributes.addFlashAttribute("message", "Only Xls or Xlsx File Allowed" + '!');
            }

        } catch (Exception e) {

            attributes.addFlashAttribute("message", "You failed uploaded " + fileName + '!');
            throw new Exception(e);
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
