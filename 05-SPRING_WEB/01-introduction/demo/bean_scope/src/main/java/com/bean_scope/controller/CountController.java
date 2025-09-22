package com.bean_scope.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.bean_scope.beans.MonitorBean;
import com.bean_scope.beans.PrototypeBean;
import com.bean_scope.beans.RequestBean;
import com.bean_scope.beans.SessionBean;
import com.bean_scope.beans.SingletonBean;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CountController {

    private final MonitorBean monitorBean;
    private final SingletonBean singletonBean;
    private final PrototypeBean prototypeBean;
    private final RequestBean requestBean;
    private final SessionBean sessionBean;

    @GetMapping
    public String getIndex(Model model) {
        model.addAttribute("monitorBean", monitorBean);
        model.addAttribute("singletonBean", singletonBean);
        model.addAttribute("prototypeBean", prototypeBean);
        model.addAttribute("requestBean", requestBean);
        model.addAttribute("sessionBean", sessionBean);
        return "index";
    }

    @PostMapping("/{type}")
    public String increase(@PathVariable String type) {
        switch (type) {
            case "singleton":
                singletonBean.increase();
                break;
            case "prototype":
                prototypeBean.increase();
                break;

            case "request":
                requestBean.increase();
                break;
            case "session":
                sessionBean.increase();
                break;
            default:
                break;
        }
        return "redirect:/";
    }
}
