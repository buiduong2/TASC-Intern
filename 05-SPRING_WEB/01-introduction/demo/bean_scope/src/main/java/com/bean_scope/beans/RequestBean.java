package com.bean_scope.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestBean extends AbstractBean {

    public RequestBean(MonitorBean monitorBean) {
        super(monitorBean);
        monitorBean.increaseRequest();
    }
}
