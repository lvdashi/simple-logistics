package com.ljh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流公司
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsCompany {
    //物流公司代码
    private String no;
    //物流公司名称
    private String com;
}
