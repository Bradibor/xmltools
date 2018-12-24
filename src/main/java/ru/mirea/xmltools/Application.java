package ru.mirea.xmltools;

import lombok.val;
import ru.mirea.xmltools.domain.Organization;
import ru.mirea.xmltools.xmlprocessing.Marshaller;
import ru.mirea.xmltools.xmlprocessing.OrganizationService;
import ru.mirea.xmltools.xmlprocessing.OrganizationTransformAdapterService;
import ru.mirea.xmltools.xmlprocessing.Unmarshaller;

import java.io.File;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("enter working dir: ");
        final String workingDir = in.nextLine();
        System.out.println("use special xml format? (y/n)");
        OrganizationService orgService = null;
        switch (in.nextLine()) {
            case "y":
            {
                System.out.println("enter input xslt template path:");
                final String templateIn = in.nextLine();
                System.out.println("enter output xslt template path:");
                final String templateOut = in.nextLine();
                orgService = new OrganizationTransformAdapterService(
                        new Marshaller(), new Unmarshaller(), workingDir,
                        new File(templateIn), new File(templateOut));
            }; break;
            case "n":
            default:  orgService = new OrganizationService(new Marshaller(), new Unmarshaller(), workingDir);
        }
        System.out.println("enter ogrn to edit: ");
        final Long ogrn = Optional.of(in.nextLine()).filter(s->!s.isEmpty()).map(Long::valueOf).orElse(null);

        final Organization org;
        if(ogrn == null) {
            System.out.println("creating new entry");
            org = new Organization();
        } else {
            assert orgService != null;
            val orgOpt = orgService.getByOgrn(ogrn);
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
            System.out.println("what do you want to change? \n1.ogrn \n2.inn \n3.kpp \n4.status\n5.name\nx - exit1");
            String changeInput = in.nextLine();
            switch (changeInput) {
                case "1": setOgrn(org, System.out, in); break;
                case "2": setInn(org, System.out, in); break;
                case "3": setKpp(org, System.out, in); break;
                case "4": setStatus(org, System.out, in); break;
                case "5": setName(org, System.out, in); break;
            }
            System.out.println(org);
            System.out.println("press any to continue, x to exit...");
        }
        System.out.println("save changes? (y/n)");
        switch (in.nextLine()) {
            case "y": orgService.save(org);
            case "n":
            default: break;
        }
//        C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources
//        12308454325
//        349032434134
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
}
