package com.telkomsigma.conveter.service.impl;

import com.telkomsigma.conveter.errorHandler.ErrorConstant;
import com.telkomsigma.conveter.errorHandler.ExceptionConvertHandler;
import com.telkomsigma.conveter.model.*;
import com.telkomsigma.conveter.service.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Service
public class ExcelServiceImpl implements ExcelService {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    ParameterServiceImpl parameterService;

    public Sheet getTypeExcel(String ext, InputStream inputStreamFile) throws IOException {
        Workbook workbook = null;
        if (ext.equalsIgnoreCase(Constant.xlsx)) {
            workbook = new XSSFWorkbook(inputStreamFile);
        } else if (ext.equalsIgnoreCase(Constant.xls)) {
            workbook = new HSSFWorkbook(inputStreamFile);
        }
        return workbook.getSheetAt(0);
    }

    @Override
    public int getLengthRow(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.iterator();
        int jumlahRow = 0;
        while (rowIterator.hasNext()) {
            rowIterator.next();
            jumlahRow++;
        }
        return jumlahRow;
    }

    @Override
    public int getLengthCell(Sheet sheet) {
        Iterator<Cell> cellIterator = sheet.getRow(0).cellIterator();
        int jumlahCell = 0;
        while (cellIterator.hasNext()) {
            cellIterator.next();
            jumlahCell++;
        }
        return jumlahCell;
    }

    @Override
    public StringBuffer insertDataFromExcel(ExcelInfo excelInfo,StringBuffer data) {
        Cell cell;
        String header = "";
        int[] rowcell = {0, 0};
        StringBuffer dataHeader = new StringBuffer();
        StringBuffer dataDetail = new StringBuffer();
        for (int k = 1; k < excelInfo.getRow(); k++) {
            rowcell[0] = k + 1;
            for (int l = 0; l < excelInfo.getCell() - 1; l++) {
                log.info("ROW : "+k+" ,CELL : "+l);
                rowcell[1] = l + 1;
                Parameter p = excelInfo.getParameters().get(excelInfo.getSheet().getRow(0).getCell(l).getStringCellValue());
                try {
                    cell = excelInfo.getSheet().getRow(k).getCell(l);

                    if (p.getKeterangan().equals(Constant.header) && k == 1) {
                        //set Header
                        log.info("HEADER");
                        dataHeader = validateCell(cell,p,rowcell,dataHeader);
                    } else if (!p.getKeterangan().equals(Constant.header)){
                        //set Detail
                        log.info("DETAIL");
                        dataDetail = validateCell(cell,p,rowcell,dataDetail);

                    }
                } catch (NullPointerException e) {
                    dataDetail.append(" ");
                    dataHeader.append(" ");
                    if (p.getMandatory().equals(Constant.mandatory)) {
                        //error mandatory
                        throw new ExceptionConvertHandler(ErrorConstant.ERROR_MANDATORY + p.getValue() + '!' +
                                ErrorConstant.ERROR_ROW + rowcell[0] +
                                ErrorConstant.ERROR_CELL + rowcell[1] + ")");
                    }else if(p.getMandatory().equals(Constant.optional)){
                    	//kalau optional skip
                    }else{
                    	// m/o 
                    	//cek catatan
                    }
                }
            }
            dataDetail.append('\n'); // appending new line after each row
        }
        //set header then detail following

        return dataHeader.append('\n').append(dataDetail);
    }

    private StringBuffer validateCell(Cell cell, Parameter p,int[] rowcell,StringBuffer data) {
        String v;
        int selisih =0;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                v = String.valueOf((int) cell.getNumericCellValue()).replaceAll("\\s", "");
                //validate param
                parameterService.validateParam(p, Cell.CELL_TYPE_NUMERIC, v, rowcell);
                //validate size
                log.info("p "+p.getPanjang()+" l "+v.length());
                if(p.getPanjang()>v.length()){
                    selisih = p.getPanjang()-v.length();
                    log.info("selisih "+selisih);
                    for (int i = 0; i <= selisih; i++) {
                        data.append(" ");
                    }
                }
                data.append(v);
                break;
            case Cell.CELL_TYPE_STRING:
                //v = cell.getStringCellValue().replaceAll("\\s", "");
                v = cell.getStringCellValue();
                parameterService.validateParam(p, Cell.CELL_TYPE_STRING, v, rowcell);
                data.append(v);
                //validate size
                if(p.getPanjang()>v.length()){
                    selisih = p.getPanjang()-v.length();
                    log.info("selisih "+selisih);
                    for (int i = 1; i <= selisih; i++) {
                        data.append(" ");
                    }
                }
                break;
            default:
                data.append(cell);
        }
        return data;
    }
}
