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
    private  int indexStartingPosition;
    private int rotationCount;
    private int ringSetting=0;
    //private boolean first;


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
        this.indexStartingPosition = other.indexStartingPosition;
        this.rotationCount = other.rotationCount;
    }

    public int getRotorId() {
        return rotorId;
    }

    public int getNotchPosition() {
        return notchPosition;
    }
    public void setRotationCount(int rotationCount) {
        this.rotationCount = rotationCount;
    }

    public char getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(char startingPosition) {
        int len=this.right.length();
        for (int i=0;i<len;i++) {
            if (this.right.charAt(i)== startingPosition) {
                this.indexStartingPosition=i;
                break;
            }
        }
        this.startingPosition = startingPosition;

    }
//    public void setFirst(boolean first) {
//        this.first = first;
//    }

    public String getRight(){
        return right;
    }

    public List<RotorMapping> getMappings() {
        return mappings;
    }

    public char forward(char inputChar) {
        int len = left.length();
        int indexInput = right.indexOf(inputChar);
        int adjustedIndex = (indexInput + indexStartingPosition) % len;
        return left.charAt(adjustedIndex);
    }



    public void rotate() {
        int len = left.length();

        indexStartingPosition = (indexStartingPosition + 1) % len;

        rotationCount = (rotationCount - 1 + len) % len;

        startingPosition = right.charAt(indexStartingPosition);
    }


    public boolean isAtNotch() {
        return rotationCount == 0;
    }


    public char backward(char inputChar) {
        int len = left.length();
        int indexInput = left.indexOf(inputChar);
        int adjustedIndex = (indexInput - indexStartingPosition + len) % len;
        return right.charAt(adjustedIndex);
    }






//    public char forward(char inputChar,boolean[] shouldAdvanceNextRotor) {
//        int length=left.length();
//
//        for (int i=indexStartingPosition; i<length+indexStartingPosition; i++) {
//            i= i % length;
//            if (this.right.charAt(i)== inputChar) {
//                return this.left.charAt(i);
//            }
//        }
//        if (shouldAdvanceNextRotor[0]) {
//            this.indexStartingPosition=(this.indexStartingPosition+1)%length;
//        }
//
//        if (this.rotationCount==indexStartingPosition){
//            shouldAdvanceNextRotor[0]=true;
//        } else {
//            shouldAdvanceNextRotor[0]=false;
//        }
//        throw new IllegalArgumentException("Character not found in rotor mappings: " + inputChar);
//    }
//
//
//
//    public char backward(char inputChar) {
//        int length=left.length();
//
//        for (int i=indexStartingPosition; i<length+indexStartingPosition; i++) {
//            i= i % length;
//            if (this.left.charAt(i)== inputChar) {
//                return this.right.charAt(i);
//            }
//        }
//        throw new IllegalArgumentException("Character not found in rotor mappings: " + inputChar);
//    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.startingPosition).append("(").append(this.rotationCount).append(")");
        return result.toString();
    }

    public void advance() {
        int length = left.length();
        this.indexStartingPosition = (this.indexStartingPosition + 1) % length;
    }
}