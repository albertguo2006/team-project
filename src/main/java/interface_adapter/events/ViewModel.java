package interface_adapter.events;

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
        this.state = State;
    }

    /**
     * Fires a property changed event for the state of this ViewModel.
     */
    public void firePropertyChange(){
        support.firePropertyChange("state", null, null);
    }

    /**
     * Fires a property changed event for the state of this ViewModel, which allows the user
     * to specify a different propertyName.
     */
    public void firePropertyChange(String propertyName){
        this.support.firePropertyChange(propertyName, null, this.state);
    }

    /**
     * Adds a PropertyChangeListener to this ViewModel.
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }
}
