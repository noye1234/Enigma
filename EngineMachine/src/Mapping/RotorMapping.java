package Mapping;

import Engine.*;

import java.io.Serializable;


public class RotorMapping implements Serializable {

    private final String inputChar;
    private final String outputChar;

    public RotorMapping(BTEPositioning jaxbPositioning) {
        this.inputChar = jaxbPositioning.getLeft();
        this.outputChar = jaxbPositioning.getRight();
    }

    public String getInputChar() {
        return inputChar;
    }

    public String getOutputChar() {
        return outputChar;
    }
}