package com.telkomsigma.conveter.service;

import com.telkomsigma.conveter.model.ExcelInfo;
import com.telkomsigma.conveter.model.Parameter;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

public interface ParameterService {

    public void validateParam(Parameter p, int cellType, String v, int[] rowcell);
    public HashMap<String,Parameter> processParam(MultipartFile param, ExcelInfo excelInfo) throws IOException;

}
