package com.example.application.views.list;

import com.example.application.security.SecurityService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("CRM");
        logo.addClassNames("text-l", "m-m");
        Button logOut = new Button("Log out", e -> securityService.logout());
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logOut);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-o", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink listView = new RouterLink("Contact Information", ListView.class);
        listView.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(listView,
                new RouterLink("Company Information", CompanyView.class),
                new RouterLink("Product Information", ProductView.class),
                //new RouterLink("Dashboard", DashboardView.class)
                new RouterLink("Invoices", InvoiceView.class)
                //new RouterLink("Meetings", CalendarView.class)
        ));
    }
}
