package ru.mirea.xmltools;

import lombok.val;
import ru.mirea.xmltools.domain.LegalEntity;
import ru.mirea.xmltools.repository.LegalEntityRepository;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("enter working dir: ");
        final String workingDir = in.nextLine();
        final LegalEntityRepository legalEntityRepository = new LegalEntityRepository(workingDir);
        System.out.println("enter ogrn to edit: ");
        final Long ogrn = Optional.of(in.nextLine()).filter(s->!s.isEmpty()).map(Long::valueOf).orElse(null);

        final LegalEntity legalEntity;
        if(ogrn == null) {
            System.out.println("creating new entry");
            legalEntity = new LegalEntity();
        } else {
            val legalEntityOpt = legalEntityRepository.getByOgrn(ogrn);
            legalEntity = legalEntityOpt.orElseGet(() -> new LegalEntity() {{
                System.out.println("legal entity not found - creating new entry");
                setOgrn(ogrn);
            }});
        }

        if(legalEntity.getOgrn() == null) {
           setOgrn(legalEntity, System.out, in);
        }
        if(legalEntity.getInn() == null) {
            setInn(legalEntity, System.out, in);
        }
        if(legalEntity.getKpp() == null) {
            setKpp(legalEntity, System.out, in);
        }
        if(legalEntity.getEntityType() == null) {
            setType(legalEntity, System.out, in);
        }
        System.out.println(legalEntity);
        System.out.println("press any to continue, x to exit...");
        while (!in.nextLine().equals("x")) {
            System.out.println("what do you want to change? \n1.ogrn \n2.inn \n3.kpp \n4.status\n5.name\nx - exit1");
            String changeInput = in.nextLine();
            switch (changeInput) {
                case "1": setOgrn(legalEntity, System.out, in); break;
                case "2": setInn(legalEntity, System.out, in); break;
                case "3": setKpp(legalEntity, System.out, in); break;
                case "4": setInn(legalEntity, System.out, in); break;
                case "5": setName(legalEntity, System.out, in); break;
            }
            System.out.println(legalEntity);
            System.out.println("press any to continue, x to exit...");
        }
        System.out.println("save changes? (y/n)");
        switch (in.nextLine()) {
            case "y": legalEntityRepository.save(legalEntity);
            case "n":
            default: break;
        }
//        C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources
//        legalEntityRepository.save(legalEntity);
    }

    private static void setOgrn(LegalEntity legalEntity, PrintStream out, Scanner in) {
        out.println("enter ogrn: ");
        legalEntity.setOgrn(Optional.of(in.nextLine()).map(Long::parseLong).orElse(0L));
    }

    private static void setInn(LegalEntity legalEntity, PrintStream out, Scanner in) {
        out.println("enter inn: ");
        legalEntity.setInn(Optional.of(in.nextLine()).orElse("777"));
    }

    private static void setKpp(LegalEntity legalEntity, PrintStream out, Scanner in) {
        out.println("enter kpp: ");
        legalEntity.setKpp(Optional.of(in.nextLine()).orElse("111"));
    }

    private static void setType(LegalEntity legalEntity, PrintStream out, Scanner in) {
        out.println("enter type (organization, entrepreneur): ");
        legalEntity.setEntityType(Optional.of(in.nextLine()).map(LegalEntity.EntityType::valueOf).orElse(LegalEntity.EntityType.organizaion));
    }

    private static void setStatus(LegalEntity legalEntity, PrintStream out, Scanner in) {
        out.println("enter status (active, eliminated): ");
        legalEntity.setStatus(Optional.of(in.nextLine()).map(LegalEntity.Status::valueOf).orElse(LegalEntity.Status.active));
    }

    private static void setName(LegalEntity legalEntity, PrintStream out, Scanner in) {
        out.println("enter full name: ");
        val name = Optional.ofNullable(legalEntity.getName()).orElse(new LegalEntity.Name());
        name.setFullName(in.nextLine());
        out.println("enter short name: ");
        name.setShortName(in.nextLine());
        legalEntity.setName(name);
    }
}
