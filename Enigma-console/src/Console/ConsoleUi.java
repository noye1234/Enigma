package Console;

import Engine.EngineImpl;
import component.Code;
import component.EnigmaRotor;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class ConsoleUi {
    private EngineImpl engine;
    private MachineData machineData;
    private boolean isConfigValid = false;
    private Scanner scanner = new Scanner(System.in);
    private boolean command_3_or_4=false;




    public ConsoleUi() {
        this.engine = new EngineImpl();
    }



    public void loadFileUi() {
        isConfigValid = false;

        System.out.print("Please enter the full path to the XML file: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine().trim();
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            try {
                List<String> errors=engine.loadXml(filePath);
                if (!errors.isEmpty()) {
                    System.out.println("Errors found in the XML configuration:");
                    for (String error : errors) {
                        System.out.println("- " + error);
                    }
                } else{
                    System.out.println("File loaded successfully.");
                    isConfigValid = true;

                }
            } catch (Exception e) {
                System.out.println("Error loading XML file: " + e.getMessage());
            }
        } else {
            System.out.println("The file does not exist. Please check the path and try again.");
        }






    }

    public void showMachineData() {
        if (!isConfigValid){
            System.out.println("No valid configuration loaded to display.");
            return;
        }
        machineData = engine.getMachineData();

        if (machineData == null) {
            System.out.println("No valid configuration loaded to display.");

        }
        else{
            System.out.println(machineData.toString());
        }
    }

    public void codeManually() {
        if (!isConfigValid){
            System.out.println("No valid configuration loaded to code manually.");
            return;
        }
        System.out.println("insert code:");
        String code = scanner.nextLine();
        if (code == null || code.isEmpty()) {
            System.out.println("No rotors input provided.");
            return;
        }
        String error=engine.codeManual(code);
        if (error==null){
            System.out.println("Code manually executed successfully.");
            command_3_or_4=true;
        }
        else{
            System.out.println("Error in manual coding: " + error);
        }

    }

    public void codeAutomatic() {
        if (!isConfigValid){
            System.out.println("No valid configuration loaded to code automatically.");
            return;
        }
        Code code=engine.codeAutomatic();
        System.out.println("Automatic coding executed successfully.");
        System.out.println("Coded Message: " + code.showCurrentCode());
        command_3_or_4=true;
    }

    public void massageProcesse(){
        if (!command_3_or_4){
            System.out.println("No coding command executed yet.");
            return;
        }
        System.out.println("Please enter the message to be processed:");
        String message = scanner.nextLine().toLowerCase();
        String error_validation=engine.validateMessage(message);
        if (error_validation!=null){
            System.out.println("Error in message validation: " + error_validation);
            return;
        }


        List<String> processed_massage_and_time=engine.process(message);
        System.out.println("Processed Message: " + processed_massage_and_time.get(0));
        System.out.println("Processing Time (nanoseconds): " + processed_massage_and_time.get(1));
    }

    public void ShowStatistics(){
        if (!isConfigValid){
            System.out.println("No valid configuration loaded to show statistics.");
            return;
        }
        System.out.println("Displaying configuration statistics.\n");

        List<String> statistics=engine.statistics();
        boolean code=true;
        for ( String stat : statistics ){
            if(code) {
                code=false;
                System.out.println("Codes: \n" + stat);

            }
            else {
                code=true;
                System.out.println("messages: \n" + stat);
            }

        }
    }

    public void loadSaveStateBonus(){
        System.out.println("insert 1 to load state, 2 to save state:");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear the newline character
        if (choice==1) {
            System.out.println("insert file path to load state:");
            String filePath = scanner.nextLine().trim();
            String error = engine.loadMachineState(filePath);
            if (error!=null){
                System.out.println("Error loading machine state: " + error);
            }
            else{
                System.out.println("Machine state loaded successfully.");
                if(engine.isConfigValid())
                    isConfigValid=true;
                if (engine.isCodeInitialized())
                    command_3_or_4=true;
            }
        }
        else {
            System.out.println("insert file path to save state:");
            String filePath = scanner.nextLine().trim();
            String message=engine.saveMachineState(filePath);
            System.out.println(message);


        }
    }

    void showMainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Enigma Machine ===");
            System.out.println("1. Load XML Configuration");
            System.out.println("2. Show Machine Data");
            System.out.println("3. Code Manually");
            System.out.println("4. Code Automatically");
            System.out.println("5. Process Message");
            System.out.println("6. Restart Code");
            System.out.println("7. Show statistics");
            System.out.println("8. Exit");
            System.out.println("9. Load/Save machine state (Bonus)");
            while (true) {
                System.out.print("Select an option: ");

                if(scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Clear the newline character

                    switch (choice) {
                        case 1:
                            loadFileUi();
                            break;
                        case 2:
                            showMachineData();
                            break;
                        case 3:
                            codeManually();
                            break;
                        case 4:
                            codeAutomatic();
                            break;
                        case 5:
                            massageProcesse();
                            break;
                        case 6:
                            engine.restartCode();
                            System.out.println(engine.getCurrentCode());

                            System.out.println("Code restarted successfully.");
                            break;
                        case 7:
                            ShowStatistics();
                            break;

                        case 8:
                            System.out.println("Exiting...");
                            return;
                        case 9:
                            loadSaveStateBonus();

                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                    break; // Exit the inner while loop after a valid input
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 7.");
                    scanner.next(); // Clear the invalid input
                }

            }
        }

    }


}
