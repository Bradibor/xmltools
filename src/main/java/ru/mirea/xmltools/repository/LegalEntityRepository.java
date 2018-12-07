package ru.mirea.xmltools.repository;

import ru.mirea.xmltools.domain.LegalEntity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static void main(String[] args) {
        LegalEntity.Founder founder = new LegalEntity.Founder() {{
            setOgrn(23L);
            setCapitalPercent(new BigDecimal(50));
        }};
        LegalEntity.Founder founder2 = new LegalEntity.Founder() {{
            setOgrn(24L);
            setCapitalPercent(new BigDecimal(50));
        }};
        LegalEntity legalEntity = new LegalEntity() {{
            setOgrn(1L);
            setInn("378");
            setKpp("2");
            setEntityType(EntityType.organizaion);
            setFounders(new ArrayList<Founder>() {{
                add(founder);
                add(founder2);
            }});
        }};
        LegalEntityRepository legalEntityRepository = new LegalEntityRepository("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources");
        legalEntityRepository.save(legalEntity);
    }
}
