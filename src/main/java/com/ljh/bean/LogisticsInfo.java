package com.ljh.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 物流信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogisticsInfo {
    //物流公司编码加单号
    private String logisticsNo;
    //缓存时间
    private Date addDate;
    //物流信息json
    private String logisticsInfo;
    //物流信息是否还会变化 1:已结束了 2:物流还没结束
    private Integer changeFlag;
}
