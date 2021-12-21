package com.telkomsigma.conveter.service;

import com.telkomsigma.conveter.model.Parameter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

public interface Converter {
    public StringBuffer processFile(MultipartFile inputFile, HashMap<String, Parameter> params, StringBuffer data) throws Exception;
    public StringBuffer convert(MultipartFile file,MultipartFile param, RedirectAttributes attributes)throws Exception;
}
