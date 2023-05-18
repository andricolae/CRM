package com.example.application.views.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

@Route(value = "calendar", layout = MainLayout.class)
@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Meetings and Events | CRM")
@PermitAll
public class CalendarView extends VerticalLayout {

    public CalendarView() {
        addClassName("invoice-view");
        setSizeFull();
        add(initCalendar());
    }

    private Component initCalendar() {
        return null;
    }
}
