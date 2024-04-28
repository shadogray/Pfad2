package at.tfr.pfad.view;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class TestBean implements Serializable {

    private String selected;

    private List<String> selections = List.of("aaaa", "bbbb", "cccc", "dddd");

    public List<String> getSelections() {
        return selections;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
