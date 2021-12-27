package com.telkomsigma.conveter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelInfo {
    public Sheet sheet;
    public int row;
    public int cell;
    public String keterangan;
    public String Value;
    public HashMap<String,Parameter> parameters;

}
