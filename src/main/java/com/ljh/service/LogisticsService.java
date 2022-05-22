package com.ljh.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ljh.bean.ApiRes;
import com.ljh.bean.LogisticsCompany;
import com.ljh.bean.LogisticsInfo;
import com.ljh.utils.JacksonUtil;
import com.ljh.utils.RocksDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 获取物流信息
 */
@Slf4j
@Service
public class LogisticsService {

    @Value("${logistics.key}")
    private String key;

    /**
     * 获取物流信息
     * @param com 物流公司编号
     * @param no 物流单号
     * @param sPhone 发件人手机号后四位（针对顺丰）
     * @param rPhone 收件人手机号后四位（针对顺丰）
     * @return
     */
    public String getLogisticsRecords(String com,String no,String sPhone,String rPhone){
        log.info("获取物流信息...");
        LogisticsInfo logisticsInfo=RocksDBUtils.getInstance().getLogisticsInfo(com+no);
        if(logisticsInfo!=null){
            long hour=DateUtil.between(logisticsInfo.getAddDate(),new Date(), DateUnit.HOUR);
            if(logisticsInfo.getChangeFlag()==2 && hour>0l){//物流还未结束，且已经是一个小时前缓存的记录了
                log.info("已缓存，但是过期了，从聚合数据获取...");
                return JuHeApi(com, no, sPhone, rPhone);
            }else {//缓存中的数据返回
                log.info("从缓存获取...");
                return logisticsInfo.getLogisticsInfo();
            }
        }else{//调用物流接口查询
            log.info("未缓存，从聚合数据获取...");
            return JuHeApi(com, no, sPhone, rPhone);
        }
    }

    /**
     * 调用聚合数据接口查询并缓存到rocksDb
     * @return
     */
    public synchronized String JuHeApi(String com,String no,String sPhone,String rPhone){
        String url= "http://v.juhe.cn/exp/index?key="+ key+"&com="+com+"&no="+no+"&senderPhone="+sPhone+"&receiverPhone="+rPhone;
        String resp= HttpRequest.get(url).execute().body();

        JSONObject jsonObject = JSONUtil.parseObj(resp);
        if(jsonObject.get("error_code",Integer.class)!=0){//获取物流信息报错
            return "";
        }else {
            ApiRes apiRes=jsonObject.get("result", ApiRes.class);
            LogisticsInfo logisticsInfo=LogisticsInfo.builder()
                    .addDate(new Date())
                    .logisticsInfo(JacksonUtil.bean2Json(apiRes.getList()))
                    .logisticsNo(com+no)
                    .changeFlag(Integer.parseInt(apiRes.getStatus()))
                    .build();
            //缓存到数据库
            RocksDBUtils.getInstance().putLogisticsInfo(com+no,logisticsInfo);
            return JacksonUtil.bean2Json(apiRes.getList());
        }
    }

    /**
     * 获取物流公司编号
     * @return
     */
    public List<LogisticsCompany> logisticsCompanyList(){
        log.info("获取物流公司清单..."+key);
        List<LogisticsCompany> companyList=RocksDBUtils.getInstance().getLogisticsCompany();//从缓存中获取物流公司清单
        if(companyList==null){
            //从接口获取
            log.info("从聚合数据获取...");
            String url= "http://v.juhe.cn/exp/com?key="+ key;
            String resp= HttpRequest.get(url).execute().body();
            log.info("聚合数据返回："+resp);
            JSONObject jsonObject = JSONUtil.parseObj(resp);
            if(jsonObject.get("error_code",Integer.class)!=0){//获取物流信息报错
                log.info("从聚合数据失败...");
                return null;
            }else {
                try {
                    String resStr=jsonObject.get("result").toString();
                    List<LogisticsCompany> logisticsCompanyList=JacksonUtil.toList(resStr);
                    //缓存到rocksDB
                    log.info("缓存物流公司信息...");
                    RocksDBUtils.getInstance().saveLogisticsCompany(logisticsCompanyList);
                    return logisticsCompanyList;
                }catch (Exception e){
                    return null;
                }
            }
        }
        log.info("从缓存中获取...");
        return companyList;
    }

    /**
     * 刷新物流公司信息
     * @return
     */
    public boolean refreshCompany(){
        try {
            String url= "http://v.juhe.cn/exp/com?key="+ key;
            String resp= HttpRequest.get(url).execute().body();
            JSONObject jsonObject = JSONUtil.parseObj(resp);
            if(jsonObject.get("error_code",Integer.class)!=0){//获取物流信息报错
                return false;
            }else{
                String resStr=jsonObject.get("result").toString();
                List<LogisticsCompany> logisticsCompanyList=JacksonUtil.toList(resStr);
                //缓存到rocksDB
                log.info("缓存物流公司信息...");
                RocksDBUtils.getInstance().saveLogisticsCompany(logisticsCompanyList);
            }
            return  true;
        }catch (Exception e){
            return false;
        }
    }
}
