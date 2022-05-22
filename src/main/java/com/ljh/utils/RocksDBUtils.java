package com.ljh.utils;

import cn.hutool.system.SystemUtil;
import com.ljh.bean.LogisticsCompany;
import com.ljh.bean.LogisticsInfo;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.List;


/**
 * 	存储工具类
 */
public class RocksDBUtils {

    private volatile static RocksDBUtils instance;

    public static RocksDBUtils getInstance() {
        if (instance == null) {
            synchronized (RocksDBUtils.class) {
                if (instance == null) {
                    instance = new RocksDBUtils();
                }
            }
        }
        return instance;
    }

    private RocksDB db;


    private RocksDBUtils() {
        openDB();
    }

    /**
     * 	打开数据库
     */
    private void openDB() {
        String dbFile ="";
        if(SystemUtil.getOsInfo().isMac()){
            dbFile=System.getProperty("user.dir")+"/db/logistics.db";
        }
        if(SystemUtil.getOsInfo().isWindows()){
            dbFile=System.getProperty("user.dir")+"\\db\\logistics.db";
        }
        try {
            db = RocksDB.open(dbFile);
        } catch (RocksDBException e) {
            throw new RuntimeException("Fail to open db ! ", e);
        }
    }


    /**
     * 添加物流信息
     * @param logisticsNo 物流公司编码加单号
     * @param logisticsInfo 物流信息
     */
    public void putLogisticsInfo(String logisticsNo, LogisticsInfo logisticsInfo) {
        try {
            db.put(SerializeUtils.serialize(logisticsNo), SerializeUtils.serialize(logisticsInfo));
        } catch (RocksDBException e) {
            throw new RuntimeException("Fail to put block ! ", e);
        }
    }

    /**
     * 查询物流信息
     * @param logisticsNo 物流公司编码加单号
     * @return
     */
    public LogisticsInfo getLogisticsInfo(String logisticsNo){
        try {
            if(db.get(SerializeUtils.serialize(logisticsNo))==null){
                return null;
            }
            LogisticsInfo info=(LogisticsInfo) SerializeUtils.deserialize((db.get(SerializeUtils.serialize(logisticsNo))));
            return info;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 缓存物流公司
     */
    public void saveLogisticsCompany(List<LogisticsCompany> logisticsCompanyList){
        try {
            //判断是否已缓存
            if(db.get(SerializeUtils.serialize("logisticsCom"))!=null){
                db.delete(SerializeUtils.serialize("logisticsCom"));
            }
            db.put(SerializeUtils.serialize("logisticsCom"), SerializeUtils.serialize(JacksonUtil.bean2Json(logisticsCompanyList)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取物流公司
     * @return
     */
    public List<LogisticsCompany> getLogisticsCompany(){
        try {
            if(db.get(SerializeUtils.serialize("logisticsCom"))==null){
                return null;
            }
            String info=(String) SerializeUtils.deserialize((db.get(SerializeUtils.serialize("logisticsCom"))));
            List<LogisticsCompany> list=JacksonUtil.toList(info);
            return list;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 	关闭数据库
     */
    public void closeDB() {
        try {
            db.close();
        } catch (Exception e) {
            throw new RuntimeException("Fail to close db ! ", e);
        }
    }

}
