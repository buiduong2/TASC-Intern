package com.bean_scope.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractBean {

    private final MonitorBean monitorBean;

    public AbstractBean(MonitorBean monitorBean) {
        this.monitorBean = monitorBean;
    }

    private int value;

    public void increase() {
        this.value++;
    }

    public void decrease() {
        this.value--;
    }

}
