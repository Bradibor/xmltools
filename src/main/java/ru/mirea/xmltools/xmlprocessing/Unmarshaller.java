package ru.mirea.xmltools.xmlprocessing;

import ru.mirea.xmltools.domain.Organization;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Unmarshaller {

    private XMLEventReader xmlEventReader;

    public Organization unmarshall(InputStream is) {
        Organization org = null;
        boolean status = false;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            xmlEventReader = xmlInputFactory.createXMLEventReader(is);
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.nextEvent();
                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if("organization".equals(startElement.getName().getLocalPart())) {
                        org = new Organization();
                        Optional.of(startElement.getAttributeByName(new QName("ogrn")).getValue())
                                .filter(s->!s.isEmpty())
                                .map(Long::parseLong)
                                .ifPresent(org::setOgrn);
                        Optional.of(startElement.getAttributeByName(new QName("inn")).getValue())
                                .filter(s->!s.isEmpty())
                                .map(Long::parseLong)
                                .ifPresent(org::setInn);
                        Optional.of(startElement.getAttributeByName(new QName("kpp")).getValue())
                                .filter(s->!s.isEmpty())
                                .map(Long::parseLong)
                                .ifPresent(org::setKpp);
                    } else if ("name".equals(startElement.getName().getLocalPart())) {
                        assert org != null;
                        Optional.ofNullable(this.getName()).ifPresent(org::setName);
                    } else if ("okveds".equals(startElement.getName().getLocalPart())) {
                        assert org != null;
                        Optional.ofNullable(this.getOkveds()).ifPresent(org::setOkveds);
                    } else if ("founders".equals(startElement.getName().getLocalPart())) {
                        assert org != null;
                        Optional.ofNullable(this.getFounders()).ifPresent(org::setFounders);
                    } else if ("leaders".equals(startElement.getName().getLocalPart())) {
                        assert org != null;
                        Optional.ofNullable(this.getLeaders()).ifPresent(org::setLeaders);
                    } else if ("status".equals(startElement.getName().getLocalPart())) {
                        status = true;
                    }
                } else if (event.isCharacters()) {
                    Characters chars = event.asCharacters();
                    if (status) Optional.ofNullable(chars.getData())
                            .map(Organization.Status::valueOf)
                            .ifPresent(org::setStatus);
                    status = false;
                } else if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals("organization")) return org;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return org;
    }

    private Organization.Name getName() throws XMLStreamException {
        Organization.Name name = new Organization.Name();
        boolean fullName = false;
        boolean shortName = false;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                switch (elementName) {
                    case "full_name": fullName = true; break;
                    case "short_name": shortName = true; break;
                    default: break;
                }
            } else if (event.isCharacters()) {
                Characters chars = event.asCharacters();
                if(fullName) {
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .ifPresent(name::setFullName);
                    fullName = false;
                } else if (shortName) {
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .ifPresent(name::setShortName);
                    shortName = false;
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("name")) return name;
            }
        }
        return name;
    }

    private List<Organization.Okved> getOkveds() throws XMLStreamException {
        List<Organization.Okved> okveds = new ArrayList<>();
        Organization.Okved okved = null;
        boolean code = false;
        boolean name = false;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                switch (elementName) {
                    case "okved": {
                        okved = new Organization.Okved();
                        Optional.ofNullable(startElement.getAttributeByName(new QName("version")))
                                .map(Attribute::getValue)
                                .ifPresent(okved::setVersion);
                        Optional.ofNullable(startElement.getAttributeByName(new QName("main")))
                                .map(Attribute::getValue)
                                .map("true"::equals)
                                .ifPresent(okved::setMain);
                        break;
                    }
                    case "code": code = true; break;
                    case "name": name = true; break;
                    default: break;
                }
            } else if (event.isCharacters()) {
                Characters chars = event.asCharacters();
                if(code) {
                    assert okved != null;
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .ifPresent(okved::setCode);
                    code = false;
                } else if (name) {
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .ifPresent(okved::setName);
                    name = false;
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("okved")) okveds.add(okved);
                else if (endElement.getName().getLocalPart().equals("okveds")) return okveds;
            }
        }
        return okveds;
    };

    private List<Organization.Founder> getFounders() throws XMLStreamException {
        List<Organization.Founder> founders = new ArrayList<>();
        Organization.Founder founder = null;
        Organization.Name name = null;
        Organization.Capital capital = null;
        boolean entityType = false;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                switch (elementName) {
                    case "founder": {
                        founder = new Organization.Founder();
                        Optional.ofNullable(startElement.getAttributeByName(new QName("ogrn")))
                                .map(Attribute::getValue)
                                .map(Long::parseLong)
                                .ifPresent(founder::setOgrn);
                        Optional.ofNullable(startElement.getAttributeByName(new QName("inn")))
                                .map(Attribute::getValue)
                                .map(Long::parseLong)
                                .ifPresent(founder::setInn);
                        break;
                    }
                    case "entity_type": entityType = true; break;
                    case "name": {
                        assert founder != null;
                        Optional.ofNullable(this.getName()).ifPresent(founder::setName);
                        break;
                    }
                    case "capital": {
                        assert founder != null;
                        Optional.ofNullable(this.getCapital()).ifPresent(founder::setCapital);
                        break;
                    }
                    default: break;
                }
            } else if (event.isCharacters()) {
                Characters chars = event.asCharacters();
                if(entityType) {
                    assert founder != null;
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .map(Organization.EntityType::valueOf)
                            .ifPresent(founder::setType);
                    entityType = false;
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("founder")) founders.add(founder);
                else if (endElement.getName().getLocalPart().equals("founders")) return founders;
            }
        }
        return founders;
    };

    private Organization.Capital getCapital() throws XMLStreamException {
        Organization.Capital capital = new Organization.Capital();
        boolean size = false;
        boolean percent = false;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                switch (elementName) {
                    case "size": size = true; break;
                    case "percent": percent = true; break;
                    default: break;
                }
            } else if (event.isCharacters()) {
                Characters chars = event.asCharacters();
                if(size) {
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .map(Long::parseLong)
                            .ifPresent(capital::setSize);
                    size = false;
                } else if (percent) {
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .map(BigDecimal::new)
                            .ifPresent(capital::setPercent);
                    percent = false;
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("capital")) return capital;
            }
        }
        return capital;
    }

    private List<Organization.Leader> getLeaders() throws XMLStreamException {
        List<Organization.Leader> leaders = new ArrayList<>();
        Organization.Leader leader = null;
        Organization.Name name = null;
        boolean entityType = false;
        boolean positionName = false;
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                switch (elementName) {
                    case "leader": {
                        leader = new Organization.Leader();
                        Optional.ofNullable(startElement.getAttributeByName(new QName("ogrn")))
                                .map(Attribute::getValue)
                                .map(Long::parseLong)
                                .ifPresent(leader::setOgrn);
                        Optional.ofNullable(startElement.getAttributeByName(new QName("inn")))
                                .map(Attribute::getValue)
                                .map(Long::parseLong)
                                .ifPresent(leader::setInn);
                        break;
                    }
                    case "entity_type": entityType = true; break;
                    case "name": {
                        assert leader != null;
                        Optional.ofNullable(this.getName()).ifPresent(leader::setName);
                        break;
                    }
                    case "position_name": positionName = true; break;
                    default: break;
                }
            } else if (event.isCharacters()) {
                Characters chars = event.asCharacters();
                if(entityType) {
                    assert leader != null;
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .map(Organization.EntityType::valueOf)
                            .ifPresent(leader::setType);
                    entityType = false;
                } else if (positionName) {
                    assert leader != null;
                    Optional.of(chars.getData())
                            .filter(s->!s.isEmpty())
                            .ifPresent(leader::setPositionName);
                    positionName = false;
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("leader")) leaders.add(leader);
                else if (endElement.getName().getLocalPart().equals("leaders")) return leaders;
            }
        }
        return leaders;
    };

    public static void main(String[] args) throws FileNotFoundException {
        Unmarshaller unmarshaller = new Unmarshaller();
        File file = Paths.get("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\349032434134.xml").toFile();

        Organization ek = unmarshaller.unmarshall(new FileInputStream(file));
        System.out.println(ek);
        Marshaller marshaller = new Marshaller();
        File fileOut = Paths.get("C:\\Users\\bradi\\IdeaProjects\\xmltools\\src\\main\\resources\\test2.xml").toFile();
        marshaller.marshal(new FileOutputStream(fileOut), ek);

    }
}
