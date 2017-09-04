package model;

import javafx.scene.control.TextInputControl;

public class WrongInputException extends Exception {
    private TextInputControl control;

    public WrongInputException(String msg, TextInputControl control) {
        super(msg);
        this.control = control;
    }

    public TextInputControl getControl() {
        return control;
    }
}
