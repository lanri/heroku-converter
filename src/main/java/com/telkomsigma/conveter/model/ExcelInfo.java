package com.telkomsigma.conveter.model;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelInfo {
    public Sheet sheet;
    public int row;
    public int cell;
    public String value;
    public HashMap<String,Parameter> parameters;

}


