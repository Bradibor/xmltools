package ru.mirea.xmltools.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class LegalEntity {
    @XmlAttribute(required = true)
    private Long ogrn;
    @XmlAttribute(required = true)
    private String inn;
    @XmlAttribute(required = true)
    private String kpp;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(EntityTypeAdapter.class)
    private EntityType entityType;
    @XmlElement()
    private Name name;
    @XmlElement()
    private List<Okved> okveds;
    @XmlElement()
    private List<Founder> founders;
    @XmlElement()
    private List<Leader> leaders;
    @XmlJavaTypeAdapter(StatusAdapter.class)
    private Status status;

    public enum Status {
        active, eliminated
    }

    public enum EntityType {
        organizaion, entrepreneur;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Okved {
        @XmlElement()
        private String code;
        @XmlElement()
        private String name;
        @XmlElement()
        private String version;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Founder {
        @XmlElement()
        private Long ogrn;
        @XmlElement()
        private BigDecimal capitalPercent;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Leader {
        @XmlElement()
        private Long ogrn;
        @XmlElement()
        private String positionName;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Name {
        @XmlElement()
        private String fullName;
        @XmlElement()
        private String shortName;
    }

    static public class EntityTypeAdapter extends XmlAdapter<String, EntityType> {

        @Override
        public EntityType unmarshal(String v) throws Exception {
            switch (v) {
                case "org":  return EntityType.organizaion;
                case "ip": return EntityType.entrepreneur;
                default: throw new IllegalArgumentException();
            }
        }

        @Override
        public String marshal(EntityType v) throws Exception {
            switch (v) {
                case organizaion: return "org";
                case entrepreneur: return "ip";
                default: throw new IllegalArgumentException();
            }
        }
    }

    static public class StatusAdapter extends XmlAdapter<String, Status> {

        @Override
        public Status unmarshal(String v) throws Exception {
            switch (v) {
                case "0":  return Status.eliminated;
                case "1": return Status.active;
                default: throw new IllegalArgumentException();
            }
        }

        @Override
        public String marshal(Status v) throws Exception {
            switch (v) {
                case active: return "1";
                case eliminated: return "0";
                default: throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public String toString() {
        return Optional.ofNullable(entityType).map(Object::toString).orElse("TYPE NOT SPECIFIED").toUpperCase() + "\n" +
                Optional.ofNullable(this.name).map(Name::getFullName).orElse("FULL NAME NOT SPECIFIED") +
                " (" + Optional.ofNullable(this.name).map(Name::getShortName).orElse("SHORT NAME NOT SPECIFIED") + ")" +
                "\nstatus:" + Optional.ofNullable(status).map(Object::toString).orElse("NOT SPECIFIED") +
                "\nogrn:" + Optional.ofNullable(ogrn).map(Object::toString).orElse("NOT SPECIFIED") +
                "; inn=" + Optional.ofNullable(inn).map(Object::toString).orElse("NOT SPECIFIED") +
                "; kpp=" + Optional.ofNullable(kpp).map(Object::toString).orElse("NOT SPECIFIED") +
                "\nfounders:" + Optional.ofNullable(founders).map(Object::toString).orElse("NOT SPECIFIED") +
                "\nleaders:" + Optional.ofNullable(leaders).map(Object::toString).orElse("NOT SPECIFIED");
    }
}
