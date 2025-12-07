package component;
import Mapping.ReflectorMapping;
import Engine.BTEReflector;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnigmaReflector implements Serializable {

    private String id;
    private final List<ReflectorMapping> reflectorMappings; // משתמש במודל החדש
    //private Map<Integer,Integer> inputToOutput;
    //private Map<Integer,Integer> outputToInput;

    private String input="";
    private String output="";
    private List<Character> alphabet;

    public void setAlphabet(List<Character> alphabet) {
        this.alphabet = alphabet;
    }


    /** Constructor for copying data from JAXB BTEReflector */
    public EnigmaReflector(BTEReflector jaxbReflector) {
        this.id = jaxbReflector.getId();

        // העתקת המיפויים
        this.reflectorMappings = jaxbReflector.getBTEReflect().stream()
                .map(ReflectorMapping::new) // נניח ש-ReflectorMapping קיימת ב-component
                .collect(Collectors.toList());

        int len=this.reflectorMappings.size();
//        for (int i=0;i<len;i++) {
//            inputToOutput.put(this.reflectorMappings.get(i).getInputIndex(),this.reflectorMappings.get(i).getOutputIndex());
//            outputToInput.put(this.reflectorMappings.get(i).getInputIndex(),this.reflectorMappings.get(i).getOutputIndex());
//        }






        this.input="";
        this.output="";
        for (int i=0;i<len;i++) {
            this.input+=this.reflectorMappings.get(i).getInputIndex();
            this.output+=this.reflectorMappings.get(i).getOutputIndex();
        }
    }
    public EnigmaReflector(EnigmaReflector other) {
        this.id = other.id;

        this.reflectorMappings = other.reflectorMappings;
        this.input = other.input;
//        this.outputToInput= other.outputToInput;
//        this.inputToOutput= other.inputToOutput;




        this.output = other.output;
        this.alphabet = other.alphabet;
    }



    public String getId() {
        return id;
    }
    public void setId(String id) {this.id=id;}

    public List<ReflectorMapping> getReflectorMappings() {
        return reflectorMappings;
    }

    public char reflect(char inputChar) {
        int inputNum = alphabet.indexOf(inputChar) + 1;
        int len = reflectorMappings.size();



        int i = 0;

        for (i = 0; i < len; i++) {

            if (reflectorMappings.get(i).getInputIndex() == inputNum) {
                return alphabet.get(reflectorMappings.get(i).getOutputIndex() - 1);
            }
            if (reflectorMappings.get(i).getOutputIndex() == inputNum) {
                return alphabet.get(reflectorMappings.get(i).getInputIndex() - 1);
            }
        }
        throw new IllegalArgumentException("Character not found in rotor mappings: " + inputChar);
    }

    @Override
    public String toString() {
        return id;
    }

}