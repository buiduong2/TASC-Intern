package com.bean_scope.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(value = "singleton")
public class MonitorBean {

    private int singletonBeanCreateCount;
    private int prototypeBeanCreateCount;
    private int requestBeanCreateCount;
    private int sessionBeanCreateCount;

    public void increaseSingleton() {
        singletonBeanCreateCount++;
    }

    public void increasePrototype() {
        prototypeBeanCreateCount++;
    }

    public void increaseRequest() {
        requestBeanCreateCount++;
    }

    public void increaseSession() {
        sessionBeanCreateCount++;
    }
}
