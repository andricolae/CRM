package com.example.application.views.list;

import com.example.application.data.service.CRMService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.LocalDate;
import java.util.Collection;

@Route(value = "calendar", layout = MainLayout.class)
@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Meetings and Events | CRM")
@PermitAll
public class CalendarView extends VerticalLayout {
    private CRMService service;

    public CalendarView(CRMService service) {
        this.service = service;
        addClassName("invoice-view");
        setSizeFull();
        add(initCalendar());
    }

    private Component initCalendar() {

        return null;
    }
}
