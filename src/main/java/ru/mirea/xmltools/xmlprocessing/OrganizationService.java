package ru.mirea.xmltools.xmlprocessing;

import ru.mirea.xmltools.domain.Organization;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

public class OrganizationService {
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private String workingDir;

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
