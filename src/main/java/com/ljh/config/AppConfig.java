package com.ljh.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author jinhuilv
 * @date 2022/05/18 19:03
 **/
@Slf4j
@Component
public class AppConfig {

    @Value("${logistics.key}")
    public static String key;

}
