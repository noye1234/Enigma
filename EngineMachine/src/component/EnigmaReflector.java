package component;
import Mapping.ReflectorMapping;
import Engine.BTEReflector;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnigmaReflector implements Serializable {

    private String id;
    private final List<ReflectorMapping> reflectorMappings; // משתמש במודל החדש

    private String input="";
    private String output="";

    private ArrayList<Integer> inputIndexes=new ArrayList<>();
    private ArrayList<Integer> outputIndexes=new ArrayList<>();
    private List<Character> alphabet;

    public void setAlphabet(List<Character> alphabet) {
        this.alphabet = alphabet;
    }


    /** Constructor for copying data from JAXB BTEReflector */
    public EnigmaReflector(BTEReflector jaxbReflector) {
        this.id = jaxbReflector.getId();

        // העתקת המיפויים
        this.reflectorMappings = jaxbReflector.getBTEReflect().stream()
                .map(ReflectorMapping::new)
                .collect(Collectors.toList());

        int len=this.reflectorMappings.size();

        this.input="";
        this.output="";
        for (int i=0;i<len;i++) {
            this.input+=this.reflectorMappings.get(i).getInputIndex();
            this.output+=this.reflectorMappings.get(i).getOutputIndex();
            this.inputIndexes.add(this.reflectorMappings.get(i).getInputIndex()-1);
            this.outputIndexes.add(this.reflectorMappings.get(i).getOutputIndex()-1);
        }
    }
    public EnigmaReflector(EnigmaReflector other) {
        this.id = other.id;

        this.reflectorMappings = other.reflectorMappings;
        this.input = other.input;

        this.output = other.output;
        this.alphabet = other.alphabet;
        this.inputIndexes = other.inputIndexes;
        this.outputIndexes = other.outputIndexes;
    }



    public String getId() {
        return id;
    }
    public void setId(String id) {this.id=id;}


//    public char reflect(char inputChar) {
//        int inputNum = alphabet.indexOf(inputChar) + 1;
//        int len = reflectorMappings.size();
//        int i = 0;
//
//        for (i = 0; i < len; i++) {
//
//            if (reflectorMappings.get(i).getInputIndex() == inputNum) {
//                return alphabet.get(reflectorMappings.get(i).getOutputIndex() - 1);
//            }
//            if (reflectorMappings.get(i).getOutputIndex() == inputNum) {
//                return alphabet.get(reflectorMappings.get(i).getInputIndex() - 1);
//            }
//        }
//        throw new IllegalArgumentException("Character not found in rotor mappings: " + inputChar);
//    }

    public int reflectIndex(int inputIndex) {
        int len = reflectorMappings.size();
        int i = 0;

        for (i = 0; i < len; i++) {

            if (this.inputIndexes.get(i) == inputIndex) {
                return this.outputIndexes.get(i);
            }
            if (this.outputIndexes.get(i) == inputIndex) {
                return this.inputIndexes.get(i);
            }
        }
        throw new IllegalArgumentException("Character not found in rotor mappings: " + inputIndex);
    }


    @Override
    public String toString() {
        return id;
    }

}