package com.telkomsigma.conveter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    private String type;
    private Integer panjang;
    private String mandatory;
    private Integer bitMsg;
    private String keterangan;
    private String catatan;
    private String value;


}
