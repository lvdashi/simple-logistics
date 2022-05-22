package com.ljh.bean;

/**
 * 物流状态
 */
public enum LogisticsStatus {
    PENDING("待查询"),
    NO_RECORD("无记录"),
    ERROR("查询异常"),
    IN_TRANSIT("运输中"),
    DELIVERING("派送中"),
    SIGNED("已签收"),
    REJECTED("拒签"),
    PROBLEM("疑难件"),
    INVALID("无效件"),
    TIMEOUT("超时件"),
    FAILED("派送失败"),
    SEND_BACK("退回"),
    TAKING("揽件");

    private final String text;
    private LogisticsStatus(final String text){
        this.text=text;
    }
    @Override
    public String toString(){
        return text;
    }
}
