package br.com.tecsus.sccubs.entities.converters;

import jakarta.persistence.AttributeConverter;

import java.sql.Date;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

public class YearMonthDateAttributeConverter
        implements AttributeConverter<YearMonth, Date> {

    @Override
    public java.sql.Date convertToDatabaseColumn(
            YearMonth attribute) {
        if (attribute != null) {
            return java.sql.Date.valueOf(
                    attribute.atDay(1)
            );
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(
            java.sql.Date dbData) {
        if (dbData != null) {
            return YearMonth.from(
                    Instant
                            .ofEpochMilli(dbData.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
            );
        }
        return null;
    }
}
