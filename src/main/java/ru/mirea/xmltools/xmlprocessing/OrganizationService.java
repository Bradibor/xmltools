package ru.mirea.xmltools.xmlprocessing;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ru.mirea.xmltools.domain.Organization;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.Files;
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
            return Optional.of(unmarshaller.unmarshall(is)).map(o->{
                o.setDesctiption(
                        Optional.ofNullable(o.getDescriptionPath())
                                .map(path->{
                                    try {
                                        return new String(Files.readAllBytes(Paths.get(workingDir, path)));
                                    } catch (IOException e) {
                                        System.out.println("Could not read description from file: " + workingDir + path);
                                    }
                                    return null;
                                }).orElse(null)
                );
                o.getFounders().stream().map(Organization.Founder::getCapital).forEach(capital -> {
                    if (capital.getXPathExpression() == null || capital.getPathToDictionary() == null) return;
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = null;
                    try {
                        builder = factory.newDocumentBuilder();
                        Document doc = builder.parse(Paths.get(workingDir, capital.getPathToDictionary()).toString());
                        XPathFactory xPathfactory = XPathFactory.newInstance();
                        XPath xpath = xPathfactory.newXPath();
                        XPathExpression expr = xpath.compile(capital.getXPathExpression());
                        String result = expr.evaluate(doc);
                        capital.setCurrency(result);
                    } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
                        e.printStackTrace();
                    }
                });
                return o;
            });
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
