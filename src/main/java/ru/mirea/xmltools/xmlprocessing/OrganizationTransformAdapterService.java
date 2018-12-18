package ru.mirea.xmltools.xmlprocessing;

import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.mirea.xmltools.domain.Organization;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

public class OrganizationTransformAdapterService extends OrganizationService {
    protected File stylesheet;

    public void setStylesheet(File stylesheet) {
        this.stylesheet = stylesheet;
    }

    public OrganizationTransformAdapterService(Marshaller marshaller, Unmarshaller unmarshaller, String workingDir, File stylesheet) {
        super(marshaller, unmarshaller, workingDir);
        this.stylesheet = stylesheet;
    }

    public OrganizationTransformAdapterService(OrganizationService service, File stylesheet) {
        super(service.marshaller, service.unmarshaller, service.workingDir);
        this.stylesheet = stylesheet;
    }

    public Optional<Organization> getCustomByOgrn(Long ogrn) {
        File file = Paths.get(workingDir, ogrn.toString() + ".xml").toFile();
        try {
            StreamSource styleSource = new StreamSource(stylesheet);
            StreamSource dataSource = new StreamSource(new FileInputStream(file));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
            OutputStream out = new FileOutputStream(new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\kek.xml"));
            Result result = new StreamResult(out);
//            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            transformer.transform(dataSource, result);

//            val org = unmarshaller.unmarshall(in);
            return Optional.empty();
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Organization> saveCustomByOgrn(Long ogrn) {
        File file = Paths.get(workingDir, ogrn.toString() + ".xml").toFile();
        try {
            InputStream is = new FileInputStream(file);
            return Optional.of(unmarshaller.unmarshall(is));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        OrganizationTransformAdapterService service = new OrganizationTransformAdapterService(
                new Marshaller(),
                new Unmarshaller(),
                "C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources",
                new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\transformer.xsl"));
        val res = service.getCustomByOgrn(349032434135L);
        System.out.println(res);
    }
}
