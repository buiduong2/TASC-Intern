package com.bean_scope.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionBean extends AbstractBean {

    public SessionBean(MonitorBean monitorBean) {
        super(monitorBean);
        monitorBean.increaseSession();
    }
}
