package ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.matthew.NauJava.domain.crypto.exception.KdfNotFoundException;

@Converter
public class KdfAlgorithmSpecConverter implements AttributeConverter<KdfAlgorithmSpec, String> {

    @Override
    public String convertToDatabaseColumn(KdfAlgorithmSpec kdfAlgorithmSpec) {
        if (kdfAlgorithmSpec == null) {
            return null;
        }
        return kdfAlgorithmSpec.getName();
    }

    @Override
    public KdfAlgorithmSpec convertToEntityAttribute(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        // Ищем среди Argon2
        for (Argon2Spec spec : Argon2Spec.values()) {
            if (spec.getName().equals(string) || spec.name().equals(string)) {
                return spec;
            }
        }

        // Ищем среди Pbkdf2
        for (Pbkdf2Spec spec : Pbkdf2Spec.values()) {
            if (spec.getName().equals(string) || spec.name().equals(string)) {
                return spec;
            }
        }

        throw new KdfNotFoundException("Неизвестный kdf-алгоритм: " + string);
    }
}
