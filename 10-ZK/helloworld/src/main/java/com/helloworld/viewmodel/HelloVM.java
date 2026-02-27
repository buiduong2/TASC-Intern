package com.helloworld.viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class HelloVM {

    private String message = "Hello Dương 🚀";

    public String getMessage() {
        return message;
    }

    @Command
    @NotifyChange("message")
    public void changeMessage() {
        message = "ZK + Spring Boot running!";
    }
}