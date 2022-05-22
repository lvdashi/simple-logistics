package com.ljh.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ApiRes {

    private String company;
    private String com;
    private String no;
    private String status;
    private String statusDetail;
    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private String datetime;
        private String remark;
        private String zone;
    }
}
