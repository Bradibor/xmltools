package ru.mirea.xmltools.xmlprocessing;

import ru.mirea.xmltools.domain.Organization;

import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

public class OrganizationService {
    protected Marshaller marshaller;
    protected Unmarshaller unmarshaller;
    protected String workingDir;

    public OrganizationService(Marshaller marshaller, Unmarshaller unmarshaller, String workingDir) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
        this.workingDir = workingDir;
    }

    public Optional<Organization> getByOgrn(Long ogrn) {
        File file = Paths.get(workingDir, ogrn.toString() + ".xml").toFile();
        try {
            InputStream is = new FileInputStream(file);
            return Optional.of(unmarshaller.unmarshall(is));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(Organization organization) {
        File file = Paths.get(workingDir, organization.getOgrn().toString() + ".xml").toFile();
        try {
            marshaller.marshal(new FileOutputStream(file), organization);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
