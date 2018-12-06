package ru.mirea.xmltools.repository;

import ru.mirea.xmltools.domain.LegalEntity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import java.nio.file.Paths;
import java.util.Optional;

public class LegalEntityRepository {
    private final String path;

    public LegalEntityRepository(String path) {
        this.path = path;
    }

    public void save(LegalEntity legalEntity) {
        try {
            File file = Paths.get(path, legalEntity.getOgrn().toString()).toFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(LegalEntity.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(legalEntity, file);
            jaxbMarshaller.marshal(legalEntity, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public Optional<LegalEntity> getByOgrn(Long ogrn) {
        try {
            File file = Paths.get(path, ogrn.toString()).toFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(LegalEntity.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return Optional.of((LegalEntity) jaxbUnmarshaller.unmarshal(file));
        } catch (JAXBException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
