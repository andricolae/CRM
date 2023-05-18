package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "flowcrmtutorial")
@PWA(name = "CUSTOM CRM APP",
        shortName = "CRM",
        offlinePath = "offline.html",
        offlineResources = {"images/logo.png", "images/offline.webp"})
public class Application implements AppShellConfigurator {

    // TODO - Future development: Calendar & Meeting Scheduling Functionality
    public static void main(String[] args) {SpringApplication.run(Application.class, args);}
}
