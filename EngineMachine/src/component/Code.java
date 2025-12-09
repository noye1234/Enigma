package component;
import java.io.Serializable;
import java.util.*;

public class Code implements Serializable {
    private List<Integer> order;
    private Map<Integer,EnigmaRotor> rotors= new HashMap<>();
    private Map<Integer,EnigmaRotor> copy_rotors= new HashMap<>();
    private EnigmaReflector reflectors;
    private int num_of_massages=0;
    private ArrayList<Long> times=new ArrayList<>();
    private ArrayList<String> orgMessages=new ArrayList<>();
    private ArrayList<String> outMessages=new ArrayList<>();
    private List<Character> alphabet;

    public void setAlphabet(List<Character> alphabet) {
        this.alphabet = alphabet;
    }

    public Code(Map<Integer,EnigmaRotor> rotors_, List<Integer> order_, EnigmaReflector reflectors_){
        this.rotors=rotors_;
        this.order=order_;
        this.reflectors=reflectors_;


        for (int i=0;i<order.size();i++){
            copy_rotors.put(order.get(i),new EnigmaRotor(rotors.get(order.get(i))));
        }


    }

    public void addOrginalMassage(String input) {
        this.orgMessages.add(input);
    }
    public void addOutMassage(String output) {
        this.outMessages.add(output);
    }
    public void addTime(long time) {
        this.times.add(time);
    }

    public String processMessage(String input){
        StringBuilder output = new StringBuilder();
        for (char ch : input.toCharArray()) {
            char processedChar = processChar(Character.toUpperCase(ch));
            output.append(processedChar);
        }
        return output.toString();
    }

    public void restart_code(){
        for (int i=0;i<order.size();i++){
            rotors.put(order.get(i),new EnigmaRotor(copy_rotors.get(order.get(i))));
        }
    }
    public char processChar(char ch) {

        advanceRotors(); // Always rotate before processing

        char currentChar = ch;

        // Forward (right → left): last rotor in order is rightmost
        for (int i = order.size() - 1; i >= 0; i--) {
            EnigmaRotor rotor = rotors.get(order.get(i));
            currentChar = rotor.forward(currentChar);
        }

        // Reflection
       // currentChar = reflectors.reflect(currentChar);
        int indexInput = rotors.get(order.get(0)).getLeft().indexOf(currentChar);
        indexInput=reflectors.reflectIndex(indexInput);
        currentChar = rotors.get(order.get(0)).getLeft().charAt(indexInput);

        // Backward (left → right): first rotor in order is leftmost
        for (int i = 0; i < order.size(); i++) {
            EnigmaRotor rotor = rotors.get(order.get(i));
            currentChar = rotor.backward(currentChar);
        }

        return currentChar;
    }




    private void advanceRotors() {
        EnigmaRotor right  = rotors.get(order.get(2));
        EnigmaRotor middle = rotors.get(order.get(1));
        EnigmaRotor left   = rotors.get(order.get(0));

        boolean RightIsNotch= right.isAtNotch();
        boolean MiddleIsNotch= middle.isAtNotch();

        // right תמיד מסתובב
        right.rotate();

        if (RightIsNotch) {
            middle.rotate();

            if (MiddleIsNotch) {
                left.rotate();
            }
        }
    }



    public String showCurrentCode(){
        return this.toString(this.rotors);
    }

    public String toString(Map<Integer,EnigmaRotor> my_rotors) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        for (Integer rotorIndex : order) {
            sb.append(rotorIndex);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // Remove last comma
        sb.append("><");
        for (Integer rotorIndex : order) {
            sb.append(my_rotors.get(rotorIndex).toString());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // Remove last comma
        sb.append("><");
        sb.append(reflectors.toString());
        sb.append(">");
        return sb.toString();

    }

    public String printMassagesData(){
        int len=this.orgMessages.size();
        StringBuilder sb = new StringBuilder();
        if (len==0){
            return "No messages processed yet.\n";
        }
        for (int i=0;i<len;i++){
            sb.append((i+1)+" <" + this.orgMessages.get(i)+">"+ " ->" +
                    " <" + this.outMessages.get(i)+"> ("+ this.times.get(i)+" ns)\n");
        }
        return sb.toString();
    }



    public int getMessagesCount(){
        return this.orgMessages.size();
    }


    public Map<Integer,EnigmaRotor> getCopyRotors(){
        return this.copy_rotors;
    }
    public  Map<Integer,EnigmaRotor> getRotors(){
        return this.rotors;
    }
}
