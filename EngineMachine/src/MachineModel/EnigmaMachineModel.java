package MachineModel;
import Engine.BTEEnigma;
import component.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnigmaMachineModel implements Serializable {

    private final List<Character> alphabet; // האותיות במכונה
    private final List<EnigmaRotor> availableRotors; // משתמש ב-EnigmaRotor
    private final List<EnigmaReflector> availableReflectors; // משתמש ב-EnigmaReflector
    private boolean run=true;


    public void changeRunMode(boolean mode){
        this.run=mode;
    }
    public boolean getRunMode(){
        return this.run;
    }
    private List<Character> createAlphabetSet(String ABC) {
        ArrayList<Character> list = new ArrayList();
        for (int i = 0; i < ABC.length(); i++) {
            list.add(ABC.charAt(i));
        }
        return list;
    }



    /** The Main Constructor/Mapper that converts the JAXB object structure into the clean Model. */
    public EnigmaMachineModel(BTEEnigma configData) {

       this.alphabet= createAlphabetSet(configData.getABC().trim());


        // המרת רשימת הרוטורים: BTERotor -> EnigmaRotor
        this.availableRotors = configData.getBTERotors().getBTERotor().stream()
                .map(EnigmaRotor::new) // קונסטרוקטור המרה

                .collect(Collectors.toList());

        // המרת רשימת הרפלקטורים: BTEReflector -> EnigmaReflector
        this.availableReflectors = configData.getBTEReflectors().getBTEReflector().stream()
                .map(EnigmaReflector::new)
                .collect(Collectors.toList());

        for (EnigmaReflector reflector : this.availableReflectors) {
            reflector.setAlphabet(this.alphabet);
        }
    }

    public boolean isInAlphabet(char ch) {
        return alphabet.contains(ch);
    }

    public int getAlphabetSize() {
        return alphabet.size();
    }

    // --- Getters נקיים וברורים ---


    public List<EnigmaRotor> getAvailableRotors() {
        return availableRotors;
    }

    public List<EnigmaReflector> getAvailableReflectors() {
        return availableReflectors;
    }
}