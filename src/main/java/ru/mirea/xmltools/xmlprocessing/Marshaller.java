package ru.mirea.xmltools.xmlprocessing;

import lombok.val;
import ru.mirea.xmltools.domain.Organization;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;


public class Marshaller {
    private XMLStreamWriter xmlStreamWriter;

    public void marshal(OutputStream out, Organization org) {
        try {
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            if (org != null) {
                xmlStreamWriter.writeStartDocument();
                xmlStreamWriter.writeStartElement("organization");
                xmlStreamWriter.writeAttribute("ogrn", org.getOgrn().toString());
                xmlStreamWriter.writeAttribute("inn", org.getInn().toString());
                xmlStreamWriter.writeAttribute("kpp", org.getKpp().toString());
                writeDescription(org.getDescriptionPath());
                writeName(org.getName());
                writeOkveds(org.getOkveds());
                writeFounders(org.getFounders());
                writeLeaders(org.getLeaders());
                writeTag("status", org.getStatus().name());
                xmlStreamWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void writeDescription(String path) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("description");
        xmlStreamWriter.writeAttribute("xmlns",  "http://www.w3.org/1999/xlink", "xlink", "http://www.w3.org/1999/xlink");
        xmlStreamWriter.writeAttribute("xlink",  "http://www.w3.org/1999/xlink", "type", "simple");
        xmlStreamWriter.writeAttribute("xlink",  "http://www.w3.org/1999/xlink", "href", path);
        xmlStreamWriter.writeAttribute("xlink",  "http://www.w3.org/1999/xlink", "show", "embed");
        xmlStreamWriter.writeEndElement();
    }

    private void writeOkveds(List<Organization.Okved> okveds) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("okveds");
        okveds.forEach(okved -> {
            try {
                xmlStreamWriter.writeStartElement("okved");
                if (okved.getVersion() != null) 
                    xmlStreamWriter.writeAttribute("version", okved.getVersion());
                if (okved.getMain() != null)
                    xmlStreamWriter.writeAttribute("main", okved.getMain().toString());
                if (okved.getCode() != null) writeTag("code", okved.getCode());
                if (okved.getName() != null) writeTag("name", okved.getName());
                xmlStreamWriter.writeEndElement();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        });
        xmlStreamWriter.writeEndElement();
    }

    private void writeName(Organization.Name name) throws XMLStreamException {
        if (name != null) {
            xmlStreamWriter.writeStartElement("name");
            String fullName = name.getFullName();
            String shortName = name.getShortName();
            if (fullName != null && !fullName.isEmpty()) writeTag("full_name", fullName);
            if (shortName != null && !shortName.isEmpty()) writeTag("short_name", shortName);
            xmlStreamWriter.writeEndElement();

        }
    }
    
    private void writeCapital (Organization.Capital capital) throws XMLStreamException {
        if (capital != null) {
            xmlStreamWriter.writeStartElement("capital");
            Long size = capital.getSize();
            BigDecimal percent = capital.getPercent();
            if (size != null) writeTag("size", size.toString());
            if (percent != null) writeTag("percent", percent.toString());
            xmlStreamWriter.writeEndElement();

        }
    }

    private void writeFounders(List<Organization.Founder> founders) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("founders");
        founders.forEach(founder -> {
            try {
                xmlStreamWriter.writeStartElement("founder");
                if (founder.getOgrn() != null)
                    xmlStreamWriter.writeAttribute("ogrn", founder.getOgrn().toString());
                if (founder.getInn() != null)
                    xmlStreamWriter.writeAttribute("main", founder.getInn().toString());
                writeName(founder.getName());
                writeTag("entity_type", founder.getType().name());
                writeCapital(founder.getCapital());
                xmlStreamWriter.writeEndElement();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        });
        xmlStreamWriter.writeEndElement();
    }
    
    private void writeLeaders(List<Organization.Leader> leaders) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("leaders");
        leaders.forEach(leader -> {
            try {
                xmlStreamWriter.writeStartElement("leader");
                if (leader.getOgrn() != null)
                    xmlStreamWriter.writeAttribute("ogrn", leader.getOgrn().toString());
                if (leader.getInn() != null)
                    xmlStreamWriter.writeAttribute("main", leader.getInn().toString());
                writeName(leader.getName());
                writeTag("entity_type", leader.getType().name());
                writeTag("position_name", leader.getPositionName());
                xmlStreamWriter.writeEndElement();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        });
        xmlStreamWriter.writeEndElement();
    }

    private void writeTag(String name, String content) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(name);
        xmlStreamWriter.writeCharacters(content);
        xmlStreamWriter.writeEndElement();
    }
}
