package ru.mirea.xmltools.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class Organization {

    private Long ogrn;
    private Long inn;
    private Long kpp;
    private Name name;
    private List<Okved> okveds;
    private List<Founder> founders;
    private List<Leader> leaders;
    private Status status;
    private String descriptionPath;
    private String desctiption;

    @Data
    public static class Name {
        private String fullName;
        private String shortName;

        @Override
        public String toString() {
            return Optional.ofNullable(fullName).orElse("НЕТ ДАННЫХ О ПОЛНОМ ИМЕНИ") +
                    " (" + Optional.ofNullable(shortName).orElse("НЕТ ДАННЫХ О КРАТКОМ ИМЕНИ") + ")";
        }
    }

    @Data
    public static class Okved {
        private String version;
        private Boolean main;
        private String code;
        private String name;

        @Override
        public String toString() {
            return "ОКВЭД " + code + " " + name;
        }
    }

    @Data
    public static class Founder {
        private Long ogrn;
        private Long inn;
        private Name name;
        private EntityType type;
        private Capital capital;

        @Override
        public String toString() {
            return  "\n\tОГРН=" + ogrn +
                    "\n\tИНН=" + inn +
                    "\n\tИмя=" + name +
                    "\n\tТип=" + type +
                    "\n\tКапитал=" + capital;
        }
    }

    @Data
    public static class Capital {
        private Long size;
        private BigDecimal percent;
        private String pathToDictionary;
        private String xPathExpression;
        private String sizeValue;
        private String currency = "";

        @Override
        public String toString() {
            return Optional.ofNullable(size).map(Objects::toString).map(size->size + currency).orElse("НЕТ ДАННЫХ") +
                    " (" + Optional.ofNullable(percent).map(Objects::toString).orElse("НЕТ ДАННЫХ") + ")";
        }
    }

    public enum EntityType {
        org, fiz, ip
    }

    public enum Status {
        active, eliminated
    }

    @Data
    public static class Leader {
        private Long ogrn;
        private Long inn;
        private Name name;
        private EntityType type;
        private String positionName;

        @Override
        public String toString() {
            return  "\n\tОГРН=" + ogrn +
                    "\n\tИНН=" + inn +
                    "\n\tИмя=" + name +
                    "\n\tТип=" + type +
                    "\n\tДолжность=" + positionName;
        }
    }

    @Override
    public String toString() {
        return "Организация\n" +
                Optional.ofNullable(this.desctiption).orElse("НЕТ ОПИСАНИЯ") + "\n" +
                Optional.ofNullable(this.name).map(Name::getFullName).orElse("НЕТ ДАННЫХ О НАЗВАНИИ") +
                " (" + Optional.ofNullable(this.name).map(Name::getShortName).orElse("НЕТ ДАННЫХ О НАЗВАНИИ") + ")" +
                "\nСтатус:" + Optional.ofNullable(status).map(Object::toString).orElse("НЕТ ДАННЫХ") +
                "\nОсновной ОКВЭД: " + okveds.stream().filter(Okved::getMain).findFirst().map(Okved::toString).orElse("НЕТ ДАННЫХ ОБ ОКВЭД") +
                "\nОГРН:" + Optional.ofNullable(ogrn).map(Object::toString).orElse("НЕТ ДАННЫХ") +
                "; ИНН=" + Optional.ofNullable(inn).map(Object::toString).orElse("НЕТ ДАННЫХ") +
                "; КПП=" + Optional.ofNullable(kpp).map(Object::toString).orElse("НЕТ ДАННЫХ") +
                "\nУчредители:" + Optional.ofNullable(founders).map(Object::toString).orElse("НЕТ ДАННЫХ") +
                "\nРуководители:" + Optional.ofNullable(leaders).map(Object::toString).orElse("НЕТ ДАННЫХ");
    }
}
