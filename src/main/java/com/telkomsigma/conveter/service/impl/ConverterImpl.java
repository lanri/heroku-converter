package com.telkomsigma.conveter.service.impl;

import com.telkomsigma.conveter.model.Constant;
import com.telkomsigma.conveter.model.ErrorConstant;
import com.telkomsigma.conveter.model.ExceptionConvertHandler;
import com.telkomsigma.conveter.model.Parameter;
import com.telkomsigma.conveter.service.Converter;
import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

@Service
public class ConverterImpl implements Converter {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final static String header = "Header";
    private final static String alphaNumeric = "AN";
    private final static String numeric = "N";
    private final static String mandatory = "M";

    @Autowired
    ParameterServiceImpl parameterService;

    @Override
    public StringBuffer convert(MultipartFile inputFile, MultipartFile param, RedirectAttributes attributes) throws Exception {
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        try {
            HashMap<String, Parameter> paramInfo = new HashMap<>();
            paramInfo = parameterService.processParam(param);

            data = processFile(inputFile, paramInfo, data);
            log.info("buffer : " + data);

            return data;
        } catch (Exception e) {
            log.info("buffers : " + data);
            throw new Exception(e);
        }

    }

    @Override
    public StringBuffer processFile(MultipartFile inputFile, HashMap<String, Parameter> params, StringBuffer data) throws Exception {

        InputStream inputStreamFile = inputFile.getInputStream();
        String ext = FilenameUtils.getExtension(inputFile.getOriginalFilename());

        Workbook workbookFile = null;
        Cell cellHeader;
        Cell cell;
        //menyimpan value row cell
        int[] rowcell = {0, 0};

        workbookFile = parameterService.getTypeExcel(workbookFile, ext, inputStreamFile);
        // Iterate through each rows from first sheet
        log.info("sheet : " + workbookFile.getNumberOfSheets());
        Sheet sheet = workbookFile.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        int jumlahRow = 0;
        while (rowIterator.hasNext()) {
            rowIterator.next();
            jumlahRow++;
        }
        log.info("jumlahRow : " + jumlahRow);
        Iterator<Cell> cellIterator = sheet.getRow(0).cellIterator();
        int jumlahCell = 0;
        while (cellIterator.hasNext()) {
            cellIterator.next();
            jumlahCell++;
        }
        log.info("jumlahCell : " + jumlahCell);

        //set Header
        for (int j = 0; j < jumlahCell; j++) {
            cellHeader = sheet.getRow(0).getCell(j);
            //data.append(cellHeader).append("\t");
        }
        //data.append('\n'); // appending new line after each row

        Iterator<Row> rowI = sheet.iterator();
        for (int k = 1; k < jumlahRow; k++) {
            log.info("row ke -: " + k);
            rowcell[0] = k + 1;
            for (int l = 0; l < jumlahCell - 1; l++) {
                rowcell[1] = l + 1;
                Parameter p = params.get(sheet.getRow(0).getCell(l).getStringCellValue());
                try {
                    cell = sheet.getRow(k).getCell(l);
                    String v;
                    if (p.getKeterangan().equals(header)) {
                        //set Header
                        log.info("TYPE : " + cell.getCellType());
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                v = String.valueOf((int) cell.getNumericCellValue()).replaceAll("\\s", "");
                                parameterService.validateParam(p, Cell.CELL_TYPE_NUMERIC, v, rowcell);
                                data.append(v);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                v = cell.getStringCellValue().replaceAll("\\s", "");
                                parameterService.validateParam(p, Cell.CELL_TYPE_STRING, v, rowcell);
                                data.append(v);
                                break;
                            default:
                                data.append(cell);
                        }
                    } else {
                        data.append("\t");
                        //set Detail
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BOOLEAN:
                                data.append(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                v = String.valueOf((int) cell.getNumericCellValue()).replaceAll("\\s", "");
                                parameterService.validateParam(p, Cell.CELL_TYPE_NUMERIC, v, rowcell);
                                data.append(v);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                v = cell.getStringCellValue().replaceAll("\\s", "");
                                parameterService.validateParam(p, Cell.CELL_TYPE_STRING, v, rowcell);
                                data.append(v);
                                break;
                            default:
                                data.append(cell).append("\t");
                        }
                    }
                } catch (NullPointerException e) {
                    data.append(" ");
                    log.info(e.getMessage());
                    if (p.getMandatory().equals(Constant.mandatory)) {
                        //error mandatory
                        throw new ExceptionConvertHandler(ErrorConstant.ERROR_MANDATORY + p.getValue() + '!' +
                                ErrorConstant.ERROR_ROW + rowcell[0] +
                                ErrorConstant.ERROR_CELL + rowcell[1] + ")");
                    }
                }
            }
            data.append('\n'); // appending new line after each row
        }

        return data;
    }


}


