package component;
import Mapping.RotorMapping;
import Engine.BTERotor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class EnigmaRotor implements Serializable {

    private final int rotorId;
    private final int notchPosition;
    private final List<RotorMapping> mappings;
    private String left="";
    private  String right="";
    private char startingPosition='O';
    private  int indexWindow;
    private int distanceFromWindow;


    /** Constructor for copying data from JAXB BTERotor */
    public EnigmaRotor(BTERotor jaxbRotor) {
        this.rotorId = jaxbRotor.getId();
        this.notchPosition = jaxbRotor.getNotch();

        this.mappings = jaxbRotor.getBTEPositioning().stream()
                .map(RotorMapping::new)
                .collect(Collectors.toList());

        int len=this.mappings.size();
        for (int i=0;i<len;i++) {
            this.left+=this.mappings.get(i).getInputChar();
            this.right+=this.mappings.get(i).getOutputChar();
        }
    }

    public EnigmaRotor(EnigmaRotor other) {
        // העתקת שדות final
        this.rotorId = other.rotorId;
        this.notchPosition = other.notchPosition;
        this.mappings = other.mappings;
        this.left = other.left;
        this.right = other.right;
        this.startingPosition = other.startingPosition;
        this.indexWindow = other.indexWindow;
        this.distanceFromWindow = other.distanceFromWindow;
    }

    public int getRotorId() {
        return rotorId;
    }

    public void setStartingPosition(char startingPosition) {
        int len=this.right.length();
        for (int i=0;i<len;i++) {
            if (this.right.charAt(i)== startingPosition) {
                this.indexWindow =i;
                break;
            }
        }
        this.startingPosition = startingPosition;
        this.distanceFromWindow = (notchPosition - indexWindow + len) % len;

    }


    public String getRight(){
        return right;
    }
    public String getLeft(){
        return left;
    }

    public List<RotorMapping> getMappings() {
        return mappings;
    }

    public char forward(char inputChar) {
        int len = left.length();
        int indexInput = right.indexOf(inputChar);
        int adjustedIndex = (indexInput + indexWindow) % len;
        System.out.println("Forward mapping in indexWindow " + indexWindow);
        return left.charAt(adjustedIndex);
    }


    public void rotate() {
        int len = left.length();

        indexWindow = (indexWindow + 1) % len;

        distanceFromWindow = (distanceFromWindow - 1 + len) % len;

        startingPosition = right.charAt(indexWindow);
    }


    public boolean isAtNotch() {
        return ( distanceFromWindow) == 0;
    }


    public char backward(char inputChar) {
        int len = left.length();
        int indexInput = left.indexOf(inputChar);
        int adjustedIndex = (indexInput - indexWindow +len) % len;
        return right.charAt(adjustedIndex);
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.startingPosition).append("(").append(this.distanceFromWindow).append(")");
        return result.toString();
    }



}