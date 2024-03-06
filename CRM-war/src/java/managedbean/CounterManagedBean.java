package managedbean;

import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "counterManagedBean")
@ViewScoped
public class CounterManagedBean implements Serializable {

    private int counter = 0;

    public CounterManagedBean() {
    }

    public int getCounter() {
        return counter;
    }

    public void increment(ActionEvent evt) {
        counter++;
    } //end increment

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
