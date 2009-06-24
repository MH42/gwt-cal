package com.bradrydzewski.gwt.calendar.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is a base class all Calendar Views (i.e. Day view, month view, list view)
 * should build upon. It defines and or implements all required methods and
 * properties.
 * @author Brad Rydzewski
 */
public abstract class CalendarView extends Composite implements HasValue<Appointment>, HasSettings {

    protected AbsolutePanel rootPanel = new AbsolutePanel();
    protected boolean layoutSuspended = false;
    protected boolean layoutPending = false;
    protected boolean sortPending = true;
    private Date date = new Date();
    private int days = 3;
    protected ArrayList<Appointment> appointments = new ArrayList<Appointment>();
    protected Appointment selectedAppointment = null;
    private CalendarSettings settings = null;

    public CalendarView(CalendarSettings settings) {
        
        initWidget(rootPanel);
        this.settings = settings;
        sinkEvents(Event.ONMOUSEDOWN);
    }
    
    public abstract void doLayout();

    public void suspendLayout() {
        layoutSuspended = true;
    }

    public void resumeLayout() {
        layoutSuspended = false;
        if (layoutPending) {
            doLayout();
        }
    }

    public CalendarSettings getSettings() {
        return settings;
    }

    public void setSettings(CalendarSettings settings) {
        this.settings = settings;
        doLayout();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date, int days) {
        this.date = date;
        this.days = days;
        doLayout();
    }

    public void setDate(Date date) {
        this.date = date;
        doLayout();
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
        doLayout();
    }

    public int getAppointmentCount() {
        return appointments.size();
    }

    public AppointmentInterface getAppointmentAtIndex(int index) {
        return appointments.get(index);
    }

    public AppointmentInterface getSelectedAppointment() {
        return selectedAppointment;
    }

    public void setSelectedAppointment(Appointment appointment) {

        // add appointment if doesn't exist
        if (!appointments.contains(appointment)) {
            appointments.add(appointment);
        }

        // de-select currently selected appointment
        if (selectedAppointment != null) {
            selectedAppointment.setSelected(false);
        }

        // set newly selected appointment
        this.selectedAppointment = appointment;
        appointment.setSelected(true);
    }

    public void updateAppointment(Appointment appointment) {

        if (!appointments.contains(appointment)) {
            appointments.add(appointment);
        }

        sortPending = true;
        doLayout();
    }

    public void clearAppointments() {
        appointments.clear();
        doLayout();
    }

    public void removeAppointment(Appointment appointment) {
        appointments.remove(appointment);
        sortPending = true;
        doLayout();
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
        this.sortPending = true;

        doLayout();
    }

    public void addAppointments(ArrayList<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            addAppointment(appointment);
        }
    }

    @Override
    public Appointment getValue() {
        return selectedAppointment;
    }

    @Override
    public void setValue(Appointment value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Appointment value, boolean fireEvents) {

        Appointment oldValue = selectedAppointment;
        Appointment newValue = value;

        // de-select currently selected appointment
        if (oldValue != null) {
            oldValue.setSelected(false);

        }

        // set newly selected appointment
        selectedAppointment = newValue;
        newValue.setSelected(true);

        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, newValue);

        }

    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Appointment> handler) {

        return addHandler(handler, ValueChangeEvent.getType());
    }
}
