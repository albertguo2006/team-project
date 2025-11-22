package interface_adapter.events.sleep;

import java.beans.PropertyChangeSupport;

public class SleepViewModel {
    private int currentDay;
    private boolean sleepSuccess;
    private String notificationMessage;
    private String errorMessage;
    private final PropertyChangeSupport propertyChangeSupport;

    public SleepViewModel() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
        int oldValue = this.currentDay;
        support.firePropertyChange("currentDay", oldValue, currentDay);
    }

    public boolean isSleepSuccess() {
        return sleepSuccess;
    }

    public void setSleepSuccess(boolean sleepSuccess) {
        this.sleepSuccess = sleepSuccess;
        support.firePropertyChange("sleepSuccess", !sleepSuccess, sleepSuccess);
    }
}
