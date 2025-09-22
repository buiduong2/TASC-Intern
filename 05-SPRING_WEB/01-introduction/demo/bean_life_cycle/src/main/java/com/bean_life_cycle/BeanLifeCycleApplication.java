package com.bean_life_cycle;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.bean_life_cycle.validation.NotValidException;
import com.bean_life_cycle.validation.TestBean;

@SpringBootApplication
public class BeanLifeCycleApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BeanLifeCycleApplication.class, args);
        TestBean testBean = context.getBean(TestBean.class);

        try {
            testBean.maxWithValidate(1, null);
        } catch (NotValidException e) {
            System.out.println(e.getMessage());
        }

        try {
            Integer num1 = 1;
            Integer num2 = null;
            testBean.maxWithOutValidate(num1, num2);
        } catch (UndeclaredThrowableException e) {
            System.out.println("Method không được bảo vệ");
        }

    }

}
