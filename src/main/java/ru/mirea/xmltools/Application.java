package ru.mirea.xmltools;

import lombok.val;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.mirea.xmltools.domain.Organization;
import ru.mirea.xmltools.xmlprocessing.Marshaller;
import ru.mirea.xmltools.xmlprocessing.OrganizationService;
import ru.mirea.xmltools.xmlprocessing.Unmarshaller;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Application {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("enter working dir: ");
        final String workingDir = in.nextLine();
        final OrganizationService legalEntityRepository = new OrganizationService(new Marshaller(), new Unmarshaller(), workingDir);
        System.out.println("enter ogrn to edit: ");
        final Long ogrn = Optional.of(in.nextLine()).filter(s->!s.isEmpty()).map(Long::valueOf).orElse(null);

        final Organization org;
        if(ogrn == null) {
            System.out.println("creating new entry");
            org = new Organization();
        } else {
            val orgOpt = legalEntityRepository.getByOgrn(ogrn);
            org = orgOpt.orElseGet(() -> new Organization() {{
                System.out.println("legal entity not found - creating new entry");
                setOgrn(ogrn);
            }});
        }

        if(org.getOgrn() == null) {
           setOgrn(org, System.out, in);
        }
        if(org.getInn() == null) {
            setInn(org, System.out, in);
        }
        if(org.getKpp() == null) {
            setKpp(org, System.out, in);
        }

        System.out.println(org);
        System.out.println("press any to continue, x to exit...");
        while (!in.nextLine().equals("x")) {
            System.out.println("what do you want to change? \n1.ogrn \n2.inn \n3.kpp \n4.status\n5.name\n6.currency\nx - exit1");
            String changeInput = in.nextLine();
            switch (changeInput) {
                case "1": setOgrn(org, System.out, in); break;
                case "2": setInn(org, System.out, in); break;
                case "3": setKpp(org, System.out, in); break;
                case "4": setStatus(org, System.out, in); break;
                case "5": setName(org, System.out, in); break;
                case "6": setCurrency(org, System.out, in, workingDir); break;
            }
            System.out.println(org);
            System.out.println("press any to continue, x to exit...");
        }
        System.out.println("save changes? (y/n)");
        switch (in.nextLine()) {
            case "y": legalEntityRepository.save(org);
            case "n":
            default: break;
        }
    }

    private static void setOgrn(Organization org, PrintStream out, Scanner in) {
        out.println("enter ogrn: ");
        org.setOgrn(Optional.of(in.nextLine()).map(Long::parseLong).orElse(0L));
    }

    private static void setInn(Organization org, PrintStream out, Scanner in) {
        out.println("enter inn: ");
        org.setInn(Optional.of(in.nextLine()).map(Long::parseLong).orElse(777L));
    }

    private static void setKpp(Organization org, PrintStream out, Scanner in) {
        out.println("enter kpp: ");
        org.setKpp(Optional.of(in.nextLine()).map(Long::parseLong).orElse(111L));
    }

    private static void setStatus(Organization org, PrintStream out, Scanner in) {
        out.println("enter status (active, eliminated): ");
        org.setStatus(Optional.of(in.nextLine()).map(Organization.Status::valueOf).orElse(Organization.Status.active));
    }

    private static void setName(Organization org, PrintStream out, Scanner in) {
        out.println("enter full name: ");
        val name = Optional.ofNullable(org.getName()).orElse(new Organization.Name());
        name.setFullName(in.nextLine());
        out.println("enter short name: ");
        name.setShortName(in.nextLine());
        org.setName(name);
    }

    private static void setCurrency(Organization org, PrintStream out, Scanner in, String workingDir) {
        out.println("select founder number to edit: ");
        for (int i = 0; i < org.getFounders().size(); i++) {
            out.println(i + " " + org.getFounders().get(i).toString());
        }

        Map<String, String> availableCurrencies = new HashMap<>();
        int founderId = Integer.parseInt(in.nextLine());
        if(founderId < 0 || founderId > org.getFounders().size() - 1) {
            out.println("Wrong founder id");
            return;
        }
        Optional.of(founderId).map(org.getFounders()::get).map(Organization.Founder::getCapital)
        .ifPresent(capital -> {
            if(capital.getPathToDictionary() == null || capital.getXPathExpression() == null) {
                out.println("no xlink attributes!");
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new File(workingDir, capital.getPathToDictionary()));
                NodeList list = document.getElementsByTagName("entity");
                int length = list.getLength();
                IntStream.range(0, list.getLength()).mapToObj(list::item).map(Node::getAttributes).forEach(attrs->{
                    availableCurrencies.put(attrs.getNamedItem("id").getTextContent(), attrs.getNamedItem("value").getTextContent());
                });
            } catch (ParserConfigurationException | IOException | SAXException e) {
                out.println("Unable to parse currency dictionary");
            }
            if(availableCurrencies.entrySet().isEmpty()) {
                out.println("No currencies found");
            } else {
                availableCurrencies.entrySet().forEach(out::println);
                out.println("Type id for currency: ");
                String id = in.nextLine();
                Optional.of(id).ifPresent(key->{
                    if (availableCurrencies.containsKey(key)) {
                        capital.setCurrency(availableCurrencies.get(key));
                        capital.setXPathExpression("/dictionary/entity[@id = '" + key +"']/@value");
                    } else {
                        out.println("Wrong id!");
                    };
                });
            }
        });


    }
}
