package Engine;
import Console.MachineData;
import component.Code;

import java.util.List;

import java.util.ArrayList;

public interface Engine {
    List<String> loadXml(String path);
    MachineData getMachineData();
    Code codeAutomatic();
    List<String> process(String message);
    List<String>  statistics();
    String codeManual(String code);
    public void restartCode();
    public void exit();
}