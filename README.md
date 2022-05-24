# simple-logistics
springboot快速集成 聚合数据查询物流服务

#### 配置yml文件：
    
    logistics:
        key: 替换成聚合数据的key

#### 获取物流公司：
    @Resource
    private LogisticsService logisticsService;
    
    //物流公司列表
    List<LogisticsCompany> logisticsCompanyList= logisticsService.logisticsCompanyList();
    
#### 查询快递信息（注意顺丰快递后两个参数需要传任意一个发件人或者收件人手机号后四位）：
     @Resource
    private LogisticsService logisticsService;
    
    //查询圆通快递
    String res=logisticsService.getLogisticsRecords("yt","YT6489157742732","","");

----------------------------------项目使用----------------------------------------

#### 🔖 添加仓库：

    <repositories>
        <repository>
            <id>maven-repository-main</id>
            <url>https://raw.github.com/lvdashi/simple-logistics/pom/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
    
#### 🔖 添加引用：

    <dependency>
            <groupId>com.ljh</groupId>
            <artifactId>simple-logistics</artifactId>
            <version>1.0</version>
        </dependency>
