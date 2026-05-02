package ru.matthew.NauJava.domain.crypto.algorithm.kdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.exception.KdfNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KdfFactory {

    private final Map<KdfAlgorithmSpec, SecretKeyGenerator> kdfMap;

    @Autowired
    public KdfFactory(List<SecretKeyGenerator> kdfList) {
        this.kdfMap = kdfList.stream()
                .collect(Collectors.toMap(
                        SecretKeyGenerator::getSpec, Function.identity()
                ));
    }

    public SecretKeyGenerator getSecretKeyGenerator(KdfAlgorithmSpec type) {
        var kdf = kdfMap.get(type);
        if (kdf == null) {
            throw new KdfNotFoundException("Неизвестный алгоритм хеширования: " + type);
        }
        return kdf;
    }
}
