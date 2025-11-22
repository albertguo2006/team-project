package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class delegates work to a PropertyChangeSupport object for managing the property change events.
 * @param <T> the type of state object contained in the model
 */

public class ViewModel<T> {
    private final String viewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private T state;
    private T previousState; // Track previous state

    public ViewModel(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public T getState(){
        return this.state;
    }

    public void setState(T State){

        this.previousState = this.state; // Store old state
        this.state = State;
    }

    /**
     * Fires a property changed event for the state of this ViewModel.
     */
    public void firePropertyChange(){
        support.firePropertyChange("state", this.previousState, this.state);
    }

    /**
     * Fires a property changed event for the state of this ViewModel, which allows the user
     * to specify a different propertyName.
     */
    public void firePropertyChange(String propertyName){
        this.support.firePropertyChange(propertyName, this.previousState, this.state);
    }

    /**
     * Adds a PropertyChangeListener to this ViewModel.
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }
}
