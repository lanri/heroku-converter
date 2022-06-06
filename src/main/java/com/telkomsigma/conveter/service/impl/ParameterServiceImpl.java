package com.telkomsigma.conveter.service.impl;

import com.telkomsigma.conveter.errorHandler.ErrorConstant;
import com.telkomsigma.conveter.errorHandler.ExceptionConvertHandler;
import com.telkomsigma.conveter.model.*;
import com.telkomsigma.conveter.service.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@Service
public class ParameterServiceImpl implements ParameterService {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());


    @Override
    public HashMap<String,Parameter> processParam(MultipartFile param, ExcelInfo excelInfo) throws IOException {

    	log.info(" param : "+param.getOriginalFilename());
    			
        HashMap<String,Parameter> params = new HashMap<>();

<<<<<<< HEAD
        log.info("value : ",excelInfo.getSheet().getRow(2).getCell(1).getStringCellValue());

        for (int k = 1; k < excelInfo.getRow(); k++) {
        	Parameter parameter = new Parameter();
            parameter.setValue(excelInfo.getSheet().getRow(k).getCell(1).getStringCellValue());
            log.info("value : ",excelInfo.getSheet().getRow(k).getCell(1).getStringCellValue());
            parameter.setType(excelInfo.getSheet().getRow(k).getCell(2).getStringCellValue());
            log.info("type : ",excelInfo.getSheet().getRow(k).getCell(2).getStringCellValue());
            parameter.setPanjang((int) excelInfo.getSheet().getRow(k).getCell(3).getNumericCellValue());
            log.info("panjang : ",(int) excelInfo.getSheet().getRow(k).getCell(3).getNumericCellValue());
            parameter.setBitMsg((int) excelInfo.getSheet().getRow(k).getCell(4).getNumericCellValue());
            parameter.setMandatory(excelInfo.getSheet().getRow(k).getCell(5).getStringCellValue());
            parameter.setKeterangan(excelInfo.getSheet().getRow(k).getCell(6).getStringCellValue());
            parameter.setCatatan(excelInfo.getSheet().getRow(k).getCell(7).getStringCellValue());
=======
        
        log.info("excelinfo"+excelInfo.toString());
log.info("excelinfo"+excelInfo.getSheet().getRow(1).getCell(0));
log.info("excelinfo"+excelInfo.getSheet().getRow(1).getCell(0).getStringCellValue());
for (int k = 1; k < excelInfo.getRow(); k++) {
            Parameter parameter = new Parameter();
            parameter.setValue(excelInfo.getSheet().getRow(k).getCell(0).getStringCellValue());
            parameter.setType(excelInfo.getSheet().getRow(k).getCell(1).getStringCellValue());
            parameter.setPanjang((int) excelInfo.getSheet().getRow(k).getCell(2).getNumericCellValue());
            //parameter.setSize((int) excelInfo.getSheet().getRow(k).getCell(3).getNumericCellValue());
            parameter.setMandatory(excelInfo.getSheet().getRow(k).getCell(3).getStringCellValue());
            parameter.setKeterangan(excelInfo.getSheet().getRow(k).getCell(4).getStringCellValue());
>>>>>>> origin/master
            params.put(parameter.getValue(),parameter);
        }
        return params;
    }


    @Override
    public void validateParam(Parameter p, int cellType, String v, int[] rowcell) {
        //validasi param type
        if (p.getType().equals(Constant.alphaNumeric)&&cellType==0) {
            throw new ExceptionConvertHandler(ErrorConstant.ERROR_NOT_ALPHANUMERIC + v + '!' +
                    ErrorConstant.ERROR_ROW+rowcell[0]+
                    ErrorConstant.ERROR_CELL+rowcell[1]+")");
        } else if (p.getType().equals(Constant.numeric) && cellType == 1) {
	            throw new ExceptionConvertHandler(ErrorConstant.ERROR_NOT_NUMERIC + v +'!'+
	                    ErrorConstant.ERROR_ROW+rowcell[0]+
	                    ErrorConstant.ERROR_CELL+rowcell[1]+")");
        }

        //validasi panjang
        if (p.getPanjang()<v.length()){
            throw new ExceptionConvertHandler(ErrorConstant.ERROR_PARAM_LENGTH+p.getPanjang()+
                    ErrorConstant.ERROR_FILE_LENGTH+ v.length()+" "+'!'+
                    ErrorConstant.ERROR_ROW+rowcell[0]+
                    ErrorConstant.ERROR_CELL+rowcell[1]+")");
        }
        
        //validasi bitMsg
        
        //validasi catatan
        
    }
    
    


}
