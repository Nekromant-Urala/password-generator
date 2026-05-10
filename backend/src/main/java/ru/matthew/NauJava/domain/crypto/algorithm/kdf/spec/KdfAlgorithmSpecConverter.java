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
        try {
            return Argon2Spec.valueOf(string);
        } catch (IllegalArgumentException e) {
            try {
                return Pbkdf2Spec.valueOf(string);
            } catch (IllegalArgumentException ex) {
                throw new KdfNotFoundException("Неизвестный kdf-алгоритм: " + string, ex);
            }
        }

    }
}
