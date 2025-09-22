package com.bean_life_cycle.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ValidationProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().getAnnotation(Valid.class) != null) {
            Object proxyBean = Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(),
                    new InvocationHandler() {
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            Method realMethod = bean.getClass().getMethod(method.getName(), method.getParameterTypes());

                            Annotation[][] parameterAnnos = realMethod.getParameterAnnotations();
                            for (int i = 0; i < parameterAnnos.length; i++) {
                                Annotation[] annos = parameterAnnos[i];
                                for (Annotation anno : annos) {
                                    System.out.println(anno.annotationType().equals(IsNotNull.class));
                                    if (anno.annotationType().equals(IsNotNull.class)) {
                                        if (args[i] == null) {
                                            System.out.println("Parameter thứ " + i + "is Null");
                                            throw new NotValidException("NULL NULL");
                                        }
                                    }
                                }
                            }

                            return method.invoke(bean, args);
                        };
                    }

            );

            return proxyBean;
        }
        return bean;
    }

}
