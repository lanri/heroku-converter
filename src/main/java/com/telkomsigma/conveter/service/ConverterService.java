package com.telkomsigma.conveter.service;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telkomsigma.conveter.model.Parameter;

public interface ConverterService {
    public StringBuffer convert(MultipartFile is,String fname, RedirectAttributes attributes)throws Exception;
}
