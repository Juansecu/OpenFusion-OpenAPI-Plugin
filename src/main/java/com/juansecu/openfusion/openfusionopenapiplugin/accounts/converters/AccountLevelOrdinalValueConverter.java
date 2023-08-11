package com.juansecu.openfusion.openfusionopenapiplugin.accounts.converters;

import java.util.stream.Stream;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountLevel;

@Converter(autoApply = true)
public class AccountLevelOrdinalValueConverter implements AttributeConverter<EAccountLevel, Integer> {
    @Override
    public Integer convertToDatabaseColumn(
        final EAccountLevel accountLevel
    ) {
        return accountLevel.getValue();
    }

    @Override
    public EAccountLevel convertToEntityAttribute(
        final Integer integer
    ) {
        return Stream.of(EAccountLevel.values())
            .filter(accountLevel -> accountLevel.getValue().equals(integer))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
