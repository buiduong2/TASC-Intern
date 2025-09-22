package com.bean_scope.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class SingletonBean extends AbstractBean {

    public SingletonBean(MonitorBean monitorBean) {
        super(monitorBean);
        monitorBean.increaseSingleton();

    }
}
