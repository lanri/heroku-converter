package com.telkomsigma.conveter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelInfo {
    private int row;
    private int cell;
    private String Value;

}
