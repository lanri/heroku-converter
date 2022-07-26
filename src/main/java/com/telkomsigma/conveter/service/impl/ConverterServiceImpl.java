package com.telkomsigma.conveter.service.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.telkomsigma.conveter.errorHandler.ErrorConstant;
import com.telkomsigma.conveter.errorHandler.ExceptionConvertHandler;
import com.telkomsigma.conveter.model.Constant;
import com.telkomsigma.conveter.model.Parameter;
import com.telkomsigma.conveter.service.ConverterService;
import com.telkomsigma.conveter.service.FileStorageService;


@Service
public class ConverterServiceImpl implements ConverterService {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    FileStorageService fileStorage;
    
	@Override
	public StringBuffer convert(MultipartFile file, String fname,RedirectAttributes attributes) throws Exception {
				
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            //validate file Xlsx or Xls
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {

            	//read multipart
            	InputStream FileData = file.getInputStream();
            	Workbook workbook = null;
                if (ext.equalsIgnoreCase(Constant.xlsx)) {
                    workbook = new XSSFWorkbook(FileData);
                } else if (ext.equalsIgnoreCase(Constant.xls)) {
                    workbook = new HSSFWorkbook(FileData);
                }
                
                //read sheet 0 to HashMap Param
                log.info(workbook.getSheetAt(0).getSheetName());
                Sheet paramSheet = workbook.getSheetAt(0);
                HashMap<String,Parameter> params = new HashMap<>();
                
                //populate parameter
                for (int k = 1; k < getLengthRow(paramSheet); k++) {
                    Parameter parameter = new Parameter();
                    parameter.setValue(paramSheet.getRow(k).getCell(0).getStringCellValue());
                    parameter.setType(paramSheet.getRow(k).getCell(1).getStringCellValue());
                    parameter.setPanjang((int) paramSheet.getRow(k).getCell(2).getNumericCellValue());
                    parameter.setBitMsg((int) paramSheet.getRow(k).getCell(3).getNumericCellValue());
                    parameter.setMandatory(paramSheet.getRow(k).getCell(4).getStringCellValue());
                    parameter.setKeterangan(paramSheet.getRow(k).getCell(5).getStringCellValue());
                    parameter.setCatatan(paramSheet.getRow(k).getCell(6).getStringCellValue());
                    //log.info(parameter.toString());
                    params.put(parameter.getValue(),parameter);
                }
                log.info(params.toString());
                
                //read sheet 1 then process with parameter
                //.info(workbook.getSheetAt(1).getSheetName());
                Sheet dataSheet = workbook.getSheetAt(1);
                StringBuffer processedData = processData(dataSheet,params);
                
                //store to filestorage
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String strDate = dateFormat.format(timestamp);
                fileStorage.store(file, processedData,fname+strDate);
            	
                attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

            } else {
                attributes.addFlashAttribute("message", "Only Xls or Xlsx File Allowed" + '!');
            }

        } catch (Exception e) {

            attributes.addFlashAttribute("message", "You failed uploaded " + fileName + '!');
            throw new Exception(e);
        }
		return null;
	}
   
    public StringBuffer processData(Sheet sheetData,HashMap<String,Parameter> parameters) {
        Cell cell;
        int[] rowcell = {0, 0};
        StringBuffer dataHeader = new StringBuffer();
        StringBuffer dataDetail = new StringBuffer();
        Parameter p = new Parameter();
        log.info("length : "+getLengthCell(sheetData));
        for (int k = 1; k < getLengthRow(sheetData); k++) {
            rowcell[0] = k + 1;
            for (int l = 0; l <= getLengthCell(sheetData)-1; l++) {
                log.info("ROW : "+k+" ,CELL : "+l);
                rowcell[1] = l + 1;
                //log.info("cell : "+sheetData.getRow(0).getCell(l).getStringCellValue());
                //p = parameters.get(sheetData.getRow(0).getCell(l).getStringCellValue());
                //log.info("PARAM : "+p.getValue());
                try {
                    cell = sheetData.getRow(k).getCell(l);
                    p = parameters.get(sheetData.getRow(0).getCell(l).getStringCellValue());
                    log.info("PARAM : "+p.getValue());
                    log.info("MANDATORY : "+p.getMandatory());
                    if (p.getKeterangan().equals(Constant.header) && k == 1) {
                        //set Header
                        log.info("HEADER");
                        dataHeader = validateCell(cell,p,rowcell,dataHeader);
                        log.info("dataHeader : "+dataHeader.toString());
                    } else if (!p.getKeterangan().equals(Constant.header)){
                        //set Detail
                        log.info("DETAIL");
                        dataDetail = validateCell(cell,p,rowcell,dataDetail);

                    }
                } catch (NullPointerException e) {
                	//log.info("----NULL----");
                    //dataDetail.append(" ");
                    //dataHeader.append(" ");
                	//log.info("Mandatory "+p.getMandatory());
                    if (p.getMandatory().equals(Constant.mandatory)) {
                        //error mandatory
                    	log.info("HAI");
                        throw new ExceptionConvertHandler(ErrorConstant.ERROR_MANDATORY + p.getValue() + '!' +
                                ErrorConstant.ERROR_ROW + rowcell[0] +
                                ErrorConstant.ERROR_CELL + rowcell[1] + ")");
                    }else {
                    	log.info("HELLO");
                    	addWhiteSpaceNullValue(dataHeader, p.getPanjang());
                    	addWhiteSpaceNullValue(dataDetail, p.getPanjang());
                    }
                }
            }
            dataDetail.append('\n'); // appending new line after each row
        }
        //set header then detail following

        return dataHeader.append('\n').append(dataDetail);
    }

    private StringBuffer addWhiteSpaceNullValue(StringBuffer data, int panjang) {
		// Adding whitespace
    	log.info("Masuk NULL optional");
    	for(int i = 0; i<panjang; i++) {
    		data.append(" ");
    	}
		return data;
	}

	private StringBuffer validateCell(Cell cell, Parameter p,int[] rowcell,StringBuffer data) {
        String v="";
        int selisih =0;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                v = String.valueOf((int)cell.getNumericCellValue()).replaceAll("\\s", "");
                log.info("data numeric : "+v);
                //validate param
                validateParam(p, 0, v, rowcell);
                //validate size
                log.info("p "+p.getPanjang()+" l "+v.length());
                if(p.getPanjang()>v.length()){
                    selisih = p.getPanjang()-v.length();
                    log.info("selisih "+selisih);
                    for (int i = 0; i < selisih; i++) {
                        data.append(" ");
                    }
                }
                data.append(v);
                break;
            case Cell.CELL_TYPE_STRING:
                //v = cell.getStringCellValue().replaceAll("\\s", "");
                v = cell.getStringCellValue();
                log.info("TYPE "+p.getType());
                log.info("data string : "+v);
                if(p.getType().equals("AN")) {
                validateParam(p, 1, v, rowcell);
                data.append(v);
                //validate size
                if(p.getPanjang()>v.length()){
                    selisih = p.getPanjang()-v.length();
                    log.info("selisih "+selisih);
                    for (int i = 0; i < selisih; i++) {
                        data.append(" ");
                    }
                }
                }else {
                	validateParam(p, 1, v, rowcell);
                	log.info("GOTCHA--------");
                	//validate size
                    if(p.getPanjang()>v.length()){
                        selisih = p.getPanjang()-v.length();
                        log.info("selisih "+selisih);
                        for (int i = 0; i < selisih; i++) {
                            data.append(" ");
                        }
                    }
                    data.append(v);
                }
                break;
            default:
            	for (int i = 0; i < p.getPanjang(); i++) {
                    data.append(" ");
                }
                //data.append(cell);
        }
        return data;
    }
    
    public int getLengthRow(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.iterator();
        int jumlahRow = 0;
        while (rowIterator.hasNext()) {
            rowIterator.next();
            jumlahRow++;
        }
        return jumlahRow;
    }

    public int getLengthCell(Sheet sheet) {
        Iterator<Cell> cellIterator = sheet.getRow(0).cellIterator();
        int jumlahCell = 0;
        while (cellIterator.hasNext()) {
            cellIterator.next();
            jumlahCell++;
        }
        return jumlahCell;
    }
	
    public void validateParam(Parameter p, int cellType, String v, int[] rowcell) {
        //validasi param type
        if (p.getType().equals(Constant.alphaNumeric)&&cellType==0) {
            throw new ExceptionConvertHandler(ErrorConstant.ERROR_NOT_ALPHANUMERIC + v + '!' +
                    ErrorConstant.ERROR_ROW+rowcell[0]+
                    ErrorConstant.ERROR_CELL+rowcell[1]+")");
        } else if (p.getType().equals(Constant.numeric) && cellType == 1) {
        	char O = 0;
        	if(v.charAt(0) == O)
        	{
        		throw new ExceptionConvertHandler(ErrorConstant.ERROR_NOT_NUMERIC + v +'!'+
                    ErrorConstant.ERROR_ROW+rowcell[0]+
                    ErrorConstant.ERROR_CELL+rowcell[1]+")");
        	}
        }

        //validasi panjang
        if (p.getPanjang()<v.length()){
            throw new ExceptionConvertHandler(ErrorConstant.ERROR_PARAM_LENGTH+p.getPanjang()+
                    ErrorConstant.ERROR_FILE_LENGTH+ v.length()+" "+'!'+
                    ErrorConstant.ERROR_ROW+rowcell[0]+
                    ErrorConstant.ERROR_CELL+rowcell[1]+")");
        }
    }
}


