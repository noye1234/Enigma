package Engine;

import MachineModel.EnigmaMachineModel;
import component.Code;
import jakarta.xml.bind.JAXBException;

import java.util.ArrayList;

public interface LoadManager {
    /**
     * Loads and unmarshalls an XML file into a Java object.
     */
    <T> T load(String path, Class<T> clazz) throws JAXBException;

   // void saveMachineState(String filePath, EnigmaMachineModel machineModel, ArrayList<Code> code);
}