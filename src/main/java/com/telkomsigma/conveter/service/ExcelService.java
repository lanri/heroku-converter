package com.telkomsigma.conveter.service;

import com.telkomsigma.conveter.model.ExcelInfo;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.InputStream;

public interface ExcelService {
    public Sheet getTypeExcel(String ext, InputStream inputStreamFile) throws IOException;
    public int getLengthRow(Sheet sheet);
    public int getLengthCell(Sheet sheet);
    public StringBuffer insertDataFromExcel(ExcelInfo excelInfo,StringBuffer data);
}
