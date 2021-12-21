package com.telkomsigma.conveter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    private String Type;
    private Integer Panjang;
    private String Mandatory;
    private String Keterangan;
    private String Value;

}
