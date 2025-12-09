package Engine;
import Console.MachineData;
import jakarta.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import MachineModel.EnigmaMachineModel;
import component.*;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class EngineImpl implements Engine, Serializable {

    private final LoadManager loadManager;
    private EnigmaMachineModel machineModel;
    private ArrayList<Code> code= new ArrayList<>();

    private int abcSize;
    private boolean isConfigValid = false;

    public EngineImpl() {
        this.loadManager = new LoadManagerImpl();
    }

    public boolean isCodeInitialized() {
        return !this.code.isEmpty();
    }
    public boolean isConfigValid() {
        return this.isConfigValid;
    }

    private boolean validateSequentialIds(Set<Integer> ids, int expectedCount) {
        if (ids.size() != expectedCount) return false;
        for (int i = 1; i <= expectedCount; i++) {
            if (!ids.contains(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateRomanIds(Set<String> ids) {
        String[] requiredIds = {"I", "II", "III", "IV", "V"};

        if (ids.size() > 5 || ids.isEmpty()) return false;

        List<String> validSet = Arrays.asList(requiredIds).subList(0, ids.size());

        for (String id : ids) {
            if (!validSet.contains(id)) return false;
        }
        return true;
    }

    // in EngineImp.java
    private boolean rule1(String path) {
        File file = new File(path);
        return file.exists() && path.toLowerCase().endsWith(".xml");
    }

    /** Rule 2: Checks if the size of the ABC alphabet is even. */
    private boolean rule2(BTEEnigma config) {
        // Validation check for nulls
        if (config == null || config.getABC() == null) {
            return false;
        }
        // Check if the length of the ABC string is even
        return config.getABC().trim().length() % 2 == 0;
    }

    /** Rule 3: Checks if at least 3 rotors are defined. */
    private boolean rule3(BTEEnigma config) {
        // Defensive check for nulls in the JAXB structure
        if (config == null || config.getBTERotors() == null || config.getBTERotors().getBTERotor() == null) {
            return false;
        }

        // Check if the size of the rotors list is at least 3
        return config.getBTERotors().getBTERotor().size() >= 3;
    }
    private boolean rule4(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTERotors() == null || config.getBTERotors().getBTERotor() == null) {
            return false;
        }

        Set<Integer> rotorIds = new HashSet<>();
        for (BTERotor rotor : config.getBTERotors().getBTERotor()) {
            rotorIds.add(rotor.getId());
        }

        return validateSequentialIds(rotorIds, config.getBTERotors().getBTERotor().size());
    }
    private boolean rule5(BTEEnigma config) {
        // Defensive check: If the configuration structure is missing, treat as invalid (or handled by other rules)
        if (config == null || config.getBTERotors() == null) {
            return false;
        }

        List<BTERotor> rotors = config.getBTERotors().getBTERotor();

        for (BTERotor rotor : rotors) {

            if (rotor.getBTEPositioning() != null) {
                Set<String> leftSet = new HashSet<>();
                Set<String> rightSet = new HashSet<>();

                // Iterate over all mapping pairs (BTEPositioning) in the current rotor
                for (BTEPositioning pos : rotor.getBTEPositioning()) {

                    // Rule 5 violation check: If Set.add() returns false, the element is a duplicate.

                    // Check Left column uniqueness
                    if (!leftSet.add(pos.getLeft())) {
                        // Found a duplicate input mapping in this rotor. Validation fails immediately.
                        return false;
                    }

                    // Check Right column uniqueness
                    if (!rightSet.add(pos.getRight())) {
                        // Found a duplicate output mapping in this rotor. Validation fails immediately.
                        return false;
                    }
                }
            } else {
                // If a rotor has no mappings, that might be a failure mode,
                // but usually, the schema ensures mappings exist. We assume
                // missing mappings are invalid for the purpose of this rule.
                return false;
            }
        }

        // If the loop completes for all rotors without returning false, the mappings are unique.
        return true;
    }
    private boolean rule6(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTERotors() == null || config.getBTERotors().getBTERotor() == null) {
            return false;
        }

        int abcLength = config.getABC().length();

        for (BTERotor rotor : config.getBTERotors().getBTERotor()) {
            int notch = rotor.getNotch();
            if (notch < 0 || notch >= abcLength) {
                return false; // Invalid notch position found
            }
        }
        return true; // All notch positions are valid
    }
    private boolean rule7(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTEReflectors() == null || config.getBTEReflectors().getBTEReflector() == null) {
            return false;
        }

        Set<String> reflectorIds = new HashSet<>();
        for (BTEReflector reflector : config.getBTEReflectors().getBTEReflector()) {
            reflectorIds.add(reflector.getId());
        }

        return validateRomanIds(reflectorIds);

    }
    private boolean rule8(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTEReflectors() == null || config.getBTEReflectors().getBTEReflector() == null) {
            return false;
        }

        for (BTEReflector reflector : config.getBTEReflectors().getBTEReflector()) {
            if (reflector.getBTEReflect() != null) {
                for (BTEReflect reflect : reflector.getBTEReflect()) {
                    if (reflect.getInput() == reflect.getOutput()) {
                        return false; // Found self-mapping
                    }
                }
            }
        }
        return true; // No self-mappings found
    }

    private List<String> validateConfiguration(BTEEnigma config) {
        List<String> errors = new ArrayList<>();

        if (config == null) {
            errors.add("XML file is empty or structure is incorrect (JAXB loading failed).");
            return errors;
        }

        String abc = config.getABC();

        // בדיקה קריטית למניעת NullPointerExceptions
        if (config.getBTERotors() == null || config.getBTEReflectors() == null) {
            errors.add("Missing required sections: BTE-Rotors or BTE-Reflectors.");
            return errors;
        }
        if (!rule2(config)) {
            errors.add("length of the ABC string is not even");
        }
        if (!rule3(config)) {
            errors.add("size of the rotors list is less then 3");
        }
        if (!rule4(config)) {
            errors.add("rotor IDs are not sequential integers");
        }
        if (!rule5(config)) {
            errors.add("one or more rotors have duplicate input/output mappings");
        }
        if (!rule6(config)) {
            errors.add("one or more rotors have invalid notch positions");
        }
        if (!rule7(config)) {
            errors.add("Reflector IDs are not valid sequential Roman numerals (I-V)");
        }
        if (!rule8(config)) {
            errors.add("one or more reflectors have self-mapping (Input equals Output)");
        }

        List<BTERotor> rotors = config.getBTERotors().getBTERotor();

        List<BTEReflector> reflectors = config.getBTEReflectors().getBTEReflector();
        Set<String> reflectorIds = new HashSet<>();

        if (reflectors.isEmpty()) {
            errors.add("Reflector definitions (BTEReflectors) are missing or empty.");
        }

        return errors;
    }

    // --- Engine Interface Implementation ---

    @Override
    public List<String> loadXml(String path) {
        BTEEnigma tempJAXBConfig = null;
        boolean attemptedLoadSucceeded = false;
        List<String> errors= new ArrayList<>();

        File xmlFile = new File(path);
        if (!xmlFile.exists() || !path.toLowerCase().endsWith(".xml")) {
            errors.add("Error: File not found or does not end with '.xml'.");
        }

        try {
            System.out.println("Loading configuration from: " + path); //remove

            tempJAXBConfig = loadManager.load(path, BTEEnigma.class);

            errors = validateConfiguration(tempJAXBConfig);

            if (errors.isEmpty()) {
                attemptedLoadSucceeded = true;

                this.machineModel = new EnigmaMachineModel(tempJAXBConfig);

                this.abcSize = this.machineModel.getAlphabetSize();
                this.isConfigValid = true;
                System.out.println("✅ Machine configuration loaded and successfully validated.");//remove
            } else {
                System.err.println("❌ XML file contains application errors:");//remove
                return errors;

            }
        } catch (JAXBException e) {
            System.err.println("❌ JAXB processing error (XML format/structure invalid).");//remove
            e.printStackTrace();
        }
        return errors;
    }

    @Override
    public MachineData getMachineData(){
        Code releventCode=null;
        String origCodeStr=null;
        String currCodeStr=null;
        int massagesCount=0;
        if (this.code!=null){
            releventCode = this.code.get(getCodeSize() - 1);
            origCodeStr=releventCode.toString(releventCode.getCopyRotors());
            currCodeStr= releventCode.toString(releventCode.getRotors());
            massagesCount=releventCode.getMessagesCount();
        }



        int sumOfRotor = machineModel.getAvailableRotors().size() ;
        int sumOfReflector = machineModel.getAvailableReflectors().size() ;

        MachineData machineData= new MachineData(sumOfRotor,sumOfReflector,massagesCount,
                origCodeStr,currCodeStr);

        return machineData;

    }


    public String part1(Map<Integer,EnigmaRotor> rotors,ArrayList<Integer> order,String[] input,int[] index ){
        String str=input[index[0]].trim();
        if (str==null){
            return "Invalid code format ";
        }

        int numOfRotors=3;
        for (int i=0;i<numOfRotors;i++){
            try {

                Integer ID = Integer.parseInt(str.trim());
                order.add(index[0], ID);
                for (EnigmaRotor r : machineModel.getAvailableRotors()) {
                    if (r.getRotorId() == ID) {
                        rotors.put(ID, r);
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                return "Invalid str ID format: " + str + "insert a number";
            }
            index[0]++;
            str=input[index[0]].trim();
            if (str==null){
                return "Invalid code format ";
            }

        }
        return null;

    }

    public String part2(Map<Integer,EnigmaRotor> rotors,ArrayList<Integer> order,String[] input,int[] index ){
        String str=input[index[0]].trim();
        if (str==null){
            return "Invalid code format ";
        }
        char CharLetter;
        int len=str.length();
        for (int i=0;i<len; i++){

            CharLetter = str.charAt(i);

            rotors.get(order.get(i)).setStartingPosition(CharLetter);

        }
        index[0]++;

        return null;
    }

    public String part3(Map<Integer,EnigmaRotor> rotors,ArrayList<Integer> order,String[] input,int[] index ) {
        EnigmaReflector reflector;
        String valueRome = input[index[0]].trim();
        int valueNumRome = (int) (valueRome.charAt(0) - '0');
        if (valueRome == null) {
            return "Invalid code format ";
        }
        try {
            String rome = NumberRome.fromInt(valueNumRome).name();
            for (EnigmaReflector r : machineModel.getAvailableReflectors()) {
                if (r.getId().equals(rome)) {
                    reflector = r;

                    reflector.setId(rome);
                    this.code.add(new Code(rotors, order, reflector));
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            return "Invalid str notch format: " + valueRome + "insert a number";
        }
        return null;


    }


    @Override
    public String codeManual(String codeInput) {

        String cleanedInput = codeInput.replaceAll("\\s", "");

        String[] rawInput = cleanedInput.split("[,(,),<,>]");

        String[] finalInput = Arrays.stream(rawInput)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        ArrayList<Integer> order = new ArrayList<>();
        Map<Integer,EnigmaRotor> rotors= new HashMap<>();

        int[] index = new int[1];
        index[0]=0;
        int j=-1;

        String error=part1(rotors,order,finalInput,index);
        if (error!=null){
            return error;
        }
        error=part2(rotors,order,finalInput,index);
        if (error!=null){
            return error;
        }

        error=part3(rotors,order,finalInput,index);
        if (error!=null){
            return error;
        }
        this.code.get(getCodeSize()-1).setAlphabet(machineModel.getAlphabet());
        return null;
    }


    @Override
    public Code codeAutomatic() {

        Map<Integer,EnigmaRotor> rotors= new HashMap<>();
        EnigmaReflector reflector;

        Random random = new Random();
        int alphabetSize = this.machineModel.getAlphabetSize();

        int requiredRotorCount = 3;
        List<Integer> rotorIDs = this.machineModel.getAvailableRotors().stream()
                .map(EnigmaRotor::getRotorId)
                .collect(Collectors.toList());

        Collections.shuffle(rotorIDs, random);
        List<Integer> activeRotorOrder = rotorIDs.subList(0, requiredRotorCount);

        for ( Integer rotorID : activeRotorOrder ) {
            for (EnigmaRotor r : machineModel.getAvailableRotors()) {
                if (r.getRotorId() == rotorID) {
                    rotors.put(rotorID, new EnigmaRotor(r));
                    rotors.get(rotorID).setStartingPosition(r.getRight().charAt(random.nextInt(alphabetSize)));
                   // rotors.get(rotorID).setDistanceFromWindow(random.nextInt(alphabetSize));
                    break;
                }
            }
        }

        List<Integer> reflectorIDs = this.machineModel.getAvailableReflectors().stream()
                .map(EnigmaReflector::getId)
                .map(romanId -> NumberRome.valueOf(romanId).toInt())
                .collect(Collectors.toList());
        Collections.shuffle(reflectorIDs, random);
        int selectedReflectorID = reflectorIDs.get(0);

        for ( EnigmaReflector r : machineModel.getAvailableReflectors() ) {
            if (r.getId().equals(NumberRome.fromInt(selectedReflectorID).name())) {
                reflector = new EnigmaReflector(r);
                reflector.setId(NumberRome.fromInt(selectedReflectorID).name());
                this.code.add(new Code(rotors, activeRotorOrder, reflector));
                break;
            }
        }
        this.code.get(getCodeSize()-1).setAlphabet(machineModel.getAlphabet());
        return this.code.get(getCodeSize()-1);

    }

    public int getCodeSize(){
        if (this.code==null){
            return 0;
        }
        return this.code.size();
    }

    @Override
    public List<String> process(String message) {
        restartCode();



        Long time_start = System.currentTimeMillis();

        String[] result =message.split(" ");
        StringBuilder processedMessage = new StringBuilder();
        String processedPart;

        for (String msgPart : result) {
            processedPart =  this.code.get(getCodeSize()-1).processMessage(msgPart);
            processedMessage.append(processedPart).append(" ");
        }


        Long time_end = System.currentTimeMillis();
        this.code.get(getCodeSize()-1).addOrginalMassage(message);
        this.code.get(getCodeSize()-1).addOutMassage(processedMessage.toString());
        this.code.get(getCodeSize()-1).addTime(time_end - time_start);


        List<String> processedMessagesAndTime = new ArrayList<>();
        processedMessagesAndTime.add(processedMessage.toString());
        processedMessagesAndTime.add(String.valueOf(time_end - time_start));


        return processedMessagesAndTime;


    }

    @Override
    public List<String> statistics() {
        List<String> stats = new ArrayList<>();
        for (Code c : this.code){
            stats.add(c.toString(c.getCopyRotors()));
            stats.add(c.printMassagesData());
        }
        return stats;
    }
    @Override
    public void exit(){
        this.machineModel.changeRunMode(false);
    }


    @Override
    public void restartCode(){
        this.code.get(getCodeSize()-1).restart_code();

    }


    public String getCurrentCode(){
        return this.code.get(getCodeSize()-1).toString(this.code.get(getCodeSize()-1).getRotors());
    }

    public String saveMachineState(String filePath){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath + ".dat"))) {
            out.writeObject(this);
            return "Machine state saved successfully to " + filePath + ".dat";
        } catch (IOException e) {
            return "Error saving machine state: " + e.getMessage();
        }
    }
    public String loadMachineState(String filePath){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            EngineImpl loadedEngine = (EngineImpl) in.readObject();
            this.machineModel = loadedEngine.machineModel;
            this.code = loadedEngine.code;
            this.abcSize = loadedEngine.abcSize;
            this.isConfigValid = loadedEngine.isConfigValid;


            return null;
        } catch (IOException | ClassNotFoundException e) {
            return "Error loading machine state: " + e.getMessage();
        }
    }

    public String validateMessage(String message){
        StringBuilder invalidChars = new StringBuilder();
        int len=message.length();
        message=message.trim().toUpperCase();

        if (message.length()==0){
            return "Error: Message is empty.";
        }

        for (int i=0;i<len;i++){
            if(!machineModel.isInAlphabet(message.charAt(i))){
                if (invalidChars.indexOf(String.valueOf(message.charAt(i))) == -1) {
                    invalidChars.append(message.charAt(i));
                }
            }
        }
        if (invalidChars.length() > 0) {
            return "Error: Message contains invalid characters not in the machine's alphabet: " + invalidChars.toString();
        }
        return null;
    }

}