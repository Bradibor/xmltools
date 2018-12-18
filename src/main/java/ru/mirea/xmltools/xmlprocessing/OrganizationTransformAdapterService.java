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
    protected File stylesheetIn;
    protected File stylesheetOut;


    public OrganizationTransformAdapterService(Marshaller marshaller, Unmarshaller unmarshaller, String workingDir, File stylesheetIn, File stylesheetOut) {
        super(marshaller, unmarshaller, workingDir);
        this.stylesheetIn = stylesheetIn;
        this.stylesheetOut = stylesheetOut;
    }

    public OrganizationTransformAdapterService(OrganizationService service, File stylesheetIn, File stylesheetOut) {
        super(service.marshaller, service.unmarshaller, service.workingDir);
        this.stylesheetIn = stylesheetIn;
        this.stylesheetOut = stylesheetOut;
    }

    public Optional<Organization> getCustomByOgrn(Long ogrn) {
        File file = Paths.get(workingDir, ogrn.toString() + ".xml").toFile();
        try {
            StreamSource styleSource = new StreamSource(stylesheetIn);
            StreamSource dataSource = new StreamSource(new FileInputStream(file));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
            File temp = new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\tempIn.xml");
            OutputStream out = new FileOutputStream(temp);
            Result result = new StreamResult(out);
            InputStream is = new FileInputStream(temp);
            transformer.transform(dataSource, result);

            val org = unmarshaller.unmarshall(is);
            return Optional.of(org);
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveCustomByOgrn(Organization org) {
        try {
            OutputStream outTemp = new FileOutputStream(new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\tempOut.xml"));
            marshaller.marshal(outTemp, org);
            StreamSource styleSource = new StreamSource(stylesheetOut);
            InputStream is = new FileInputStream(new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\tempOut.xml"));
            StreamSource dataSource = new StreamSource(is);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
            OutputStream out = new FileOutputStream(new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\"+org.getOgrn()+".xml"));
            Result result = new StreamResult(out);
            transformer.transform(dataSource, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OrganizationTransformAdapterService service = new OrganizationTransformAdapterService(
                new Marshaller(),
                new Unmarshaller(),
                "C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources",
                new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\transformerIn.xsl"),
                new File("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\transformerOut.xsl"));
        val res = service.getCustomByOgrn(349032434135L);
        System.out.println(res);
        service.saveCustomByOgrn(res.get());
    }
}
