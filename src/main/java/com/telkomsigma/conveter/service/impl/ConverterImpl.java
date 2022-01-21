package com.telkomsigma.conveter.service.impl;

import com.telkomsigma.conveter.model.Constant;
import com.telkomsigma.conveter.model.ExcelInfo;
import com.telkomsigma.conveter.model.Parameter;
import com.telkomsigma.conveter.service.Converter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class ConverterImpl implements Converter {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    ParameterServiceImpl parameterService;

    @Autowired
    ExcelServiceImpl excelService;

    @Override
    public StringBuffer convert(MultipartFile inputFile, MultipartFile param, RedirectAttributes attributes) throws Exception {
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        try {
            HashMap<String, Parameter> paramInfo = new HashMap<>();
            InputStream inputStreamFile = param.getInputStream();
            Sheet sheet = excelService.getTypeExcel(
                    FilenameUtils.getExtension(param.getOriginalFilename())
                    ,inputStreamFile);

            ExcelInfo excelInfo = new ExcelInfo();
            excelInfo.setSheet(sheet);
            excelInfo.setRow(excelService.getLengthRow(sheet));
            excelInfo.setCell(excelService.getLengthCell(sheet));

            paramInfo = parameterService.processParam(param,excelInfo);

            data = processFile(inputFile, paramInfo, data);
            //log.info("buffer : " + data);

            return data;
        } catch (Exception e) {
            //log.info("buffers : " + data);
            throw new Exception(e);
        }

    }

    @Override
    public StringBuffer processFile(MultipartFile inputFile, HashMap<String, Parameter> params, StringBuffer data) throws Exception {

        InputStream inputStreamFile = inputFile.getInputStream();

        // Iterate through each rows from first sheet
        Sheet sheet = excelService.getTypeExcel(FilenameUtils.getExtension(inputFile.getOriginalFilename())
                , inputStreamFile);

        ExcelInfo excelInfo = new ExcelInfo();
        excelInfo.setSheet(sheet);
        excelInfo.setParameters(params);
        excelInfo.setRow(excelService.getLengthRow(sheet));
        excelInfo.setCell(excelService.getLengthCell(sheet));

        data = excelService.insertDataFromExcel(excelInfo, data);

        return data;
    }


}


