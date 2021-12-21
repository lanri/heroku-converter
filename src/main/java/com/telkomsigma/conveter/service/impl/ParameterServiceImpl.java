package com.telkomsigma.conveter.service.impl;

import com.telkomsigma.conveter.model.Constant;
import com.telkomsigma.conveter.model.ErrorConstant;
import com.telkomsigma.conveter.model.ExceptionConvertHandler;
import com.telkomsigma.conveter.model.Parameter;
import com.telkomsigma.conveter.service.ParameterService;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

@Service
public class ParameterServiceImpl implements ParameterService {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public HashMap<String,Parameter> processParam(MultipartFile param) throws IOException {
            InputStream inputStreamFile = param.getInputStream();
        String ext = FilenameUtils.getExtension(param.getOriginalFilename());

        Workbook workbookParam = null;
        workbookParam = getTypeExcel(workbookParam,ext,inputStreamFile);

        Row row;


        Sheet sheetp = workbookParam.getSheetAt(0);
        Iterator<Row> rowIterator = sheetp.iterator();
        int jumlahRow = 0;
        while (rowIterator.hasNext()) {
            rowIterator.next();
            jumlahRow++;
        }
        //log.info("jumlahRow : " + jumlahRow);

        Iterator<Cell> cellIterator = sheetp.getRow(0).cellIterator();
        int jumlahCell = 0;
        while (cellIterator.hasNext()) {
            cellIterator.next();
            jumlahCell++;
        }
        //log.info("jumlahCell : " + jumlahCell);

        HashMap<String,Parameter> params = new HashMap<>();

        for (int k = 1; k < jumlahRow; k++) {
            Parameter parameter = new Parameter();
            parameter.setValue(sheetp.getRow(k).getCell(0).getStringCellValue());
            parameter.setType(sheetp.getRow(k).getCell(1).getStringCellValue());
            parameter.setPanjang((int) sheetp.getRow(k).getCell(2).getNumericCellValue());
            parameter.setMandatory(sheetp.getRow(k).getCell(3).getStringCellValue());
            parameter.setKeterangan(sheetp.getRow(k).getCell(4).getStringCellValue());
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
    }

    public Workbook getTypeExcel(Workbook workbookFile, String ext,InputStream inputStreamFile) throws IOException {
        if (ext.equalsIgnoreCase(Constant.xlsx)) {
            workbookFile = new XSSFWorkbook(inputStreamFile);
        } else if (ext.equalsIgnoreCase(Constant.xls)) {
            workbookFile = new HSSFWorkbook(inputStreamFile);
        }
        return workbookFile;
    }
}
