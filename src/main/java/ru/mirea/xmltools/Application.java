package ru.mirea.xmltools;

import lombok.val;
import ru.mirea.xmltools.domain.LegalEntity;
import ru.mirea.xmltools.repository.LegalEntityRepository;

import java.util.Optional;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("enter working dir: ");
        final String workingDir = in.nextLine();
        System.out.println("enter ogrn to edit: ");
        final Long ogrn = Optional.of(in.nextLine()).filter(s->!s.isEmpty()).map(Long::valueOf).orElse(null);

        final LegalEntityRepository legalEntityRepository = new LegalEntityRepository("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources");
        final LegalEntity legalEntity = new LegalEntity() {{
            setOgrn(302032341L);
            setInn("777198");
            setEntityType(LegalEntity.EntityType.organizaion);
            setKpp("23409");
            setStatus(LegalEntity.Status.active);
        }};
//        legalEntityRepository.save(legalEntity);
        val legalEntityGet = legalEntityRepository.getByOgrn(302032341L);
        legalEntityGet.ifPresent(System.out::println);
    }
}
