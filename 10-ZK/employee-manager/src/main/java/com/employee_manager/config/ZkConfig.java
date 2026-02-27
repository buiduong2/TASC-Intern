package com.employee_manager.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;

@Configuration
public class ZkConfig {

    @Bean
    public ServletRegistrationBean<DHtmlLayoutServlet> zkLoader() {

        ServletRegistrationBean<DHtmlLayoutServlet> bean = new ServletRegistrationBean<>(new DHtmlLayoutServlet(),
                "*.zul");

        bean.setLoadOnStartup(1);

        Map<String, String> params = new HashMap<>();
        params.put("update-uri", "/zkau");
        bean.setInitParameters(params);

        return bean;
    }

    @Bean
    public ServletRegistrationBean<DHtmlUpdateServlet> zkUpdateServlet() {
        return new ServletRegistrationBean<>(new DHtmlUpdateServlet(), "/zkau/*");
    }

}
