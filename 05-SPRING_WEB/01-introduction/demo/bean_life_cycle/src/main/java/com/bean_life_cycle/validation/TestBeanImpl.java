package com.bean_life_cycle.validation;

import org.springframework.stereotype.Component;

@Component
@Valid
public class TestBeanImpl implements TestBean {

    public int maxWithValidate(@IsNotNull Integer num1, @IsNotNull Integer num2) {
        if (num1 == null || num2 == null) {
            throw new NullPointerException("DIRTY NULL");
        }
        return Math.max(num1, num2);
    }

    public int maxWithOutValidate(Integer num1, Integer num2) {
        if (num1 == null || num2 == null) {
            throw new NullPointerException("DIRTY NULL");
        }
        return Math.max(num1, num2);
    }
}
