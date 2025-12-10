package Engine;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

import java.io.Serializable;

public class LoadManagerImpl implements LoadManager, Serializable {

    @Override
    public <T> T load(String path, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        File file = new File(path);

        @SuppressWarnings("unchecked")
        T loadedObject = (T) unmarshaller.unmarshal(file);

        return loadedObject;
    }



}