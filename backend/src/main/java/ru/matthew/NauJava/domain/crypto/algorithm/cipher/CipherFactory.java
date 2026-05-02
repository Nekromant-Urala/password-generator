package ru.matthew.NauJava.domain.crypto.algorithm.cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.exception.CipherNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CipherFactory {

    private final Map<CipherAlgorithmSpec, SymmetricCipher> cipherMap;

    @Autowired
    public CipherFactory(List<SymmetricCipher> cipherList) {
        this.cipherMap = cipherList.stream()
                .collect(Collectors.toMap(
                        SymmetricCipher::getSpec, Function.identity()
                ));
    }

    public SymmetricCipher getCipher(CipherAlgorithmSpec type) {
        var cipher = cipherMap.get(type);
        if (cipher == null) {
            throw new CipherNotFoundException("Неизвестный алгоритм шифрования: " + type);
        }
        return cipher;
    }
}
