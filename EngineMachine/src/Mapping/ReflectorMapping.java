package Mapping;
import Engine.*;

import java.io.Serializable;

public class ReflectorMapping implements Serializable {

    private final int inputIndex;
    private final int outputIndex;

    /** Constructor for copying data from JAXB BTEReflect */
    public ReflectorMapping(BTEReflect jaxbReflect) {
        this.inputIndex = jaxbReflect.getInput();
        this.outputIndex = jaxbReflect.getOutput();
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public int getOutputIndex() {
        return outputIndex;
    }
}