package com.office_request_demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.lang.Library;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;

@Configuration
public class ZkConfig {

    @Bean
    public ServletRegistrationBean<DHtmlLayoutServlet> zkServlet() {
        ServletRegistrationBean<DHtmlLayoutServlet> bean = new ServletRegistrationBean<>(new DHtmlLayoutServlet(),
                "*.zul");

        bean.setLoadOnStartup(1);

        Map<String, String> params = new HashMap<>();
        params.put("update-uri", "/zkau");
        bean.setInitParameters(params);

        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> httpSessionListener() {
        return new ServletListenerRegistrationBean<>(new HttpSessionListener());
    }

    @Bean
    public ServletRegistrationBean<DHtmlUpdateServlet> zkUpdateServlet() {
        return new ServletRegistrationBean<>(new DHtmlUpdateServlet(), "/zkau/*");
    }

    @PostConstruct
    public void init() {
        Library.setProperty("org.zkoss.web.preferred.locale", "en_US");
    }
}
