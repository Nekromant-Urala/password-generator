package ru.matthew.NauJava.domain.crypto.generation;

import org.springframework.stereotype.Service;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileForPasswordDto;

import java.security.SecureRandom;
import java.util.ArrayList;

import static ru.matthew.NauJava.domain.crypto.generation.Symbol.*;

@Service
public class RandomGeneratorServiceImpl implements RandomGeneratorService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public char[] generatePassword(GeneratorProfileForPasswordDto dto) {
        int passwordLength = dto.passwordLength();

        int requiredTypeChar = countRequiredTypeChar(dto);

        if (passwordLength < 1 || passwordLength < requiredTypeChar) {
            throw new IllegalArgumentException("Длина пароля должна быть больше");
        }
        char[] password = new char[passwordLength];

        StringBuilder pool = new StringBuilder();
        ArrayList<Character> guaranteedChars = new ArrayList<>();

        if (dto.isUppercase()) {
            pool.append(UPPERCASE.getChars());
            guaranteedChars.add(getRandomChar(UPPERCASE.getChars()));
        }
        if (dto.isLowercase()) {
            pool.append(LOWERCASE.getChars());
            guaranteedChars.add(getRandomChar(LOWERCASE.getChars()));
        }
        if (dto.isDigits()) {
            pool.append(DIGITS.getChars());
            guaranteedChars.add(getRandomChar(DIGITS.getChars()));
        }
        if (dto.isSpecialChars()) {
            pool.append(SPECIAL_CHARS.getChars());
            guaranteedChars.add(getRandomChar(SPECIAL_CHARS.getChars()));
        }
        if (!dto.customChars().isEmpty()) {
            pool.append(dto.customChars());
        }

        int currentIdx = 0;
        for (var c : guaranteedChars) {
            password[currentIdx++] = c;
        }

        String fullPool = pool.toString();

        if (!dto.isDuplicateChars()) {
            if (pool.length() < passwordLength) {
                throw new IllegalArgumentException("Уникальных символов в пуле меньше, чем требуемая длина пароля.");
            }

            char[] poolArray = fullPool.toCharArray();
            shuffle(poolArray);

            System.arraycopy(poolArray, currentIdx, password, currentIdx, passwordLength - currentIdx);
        } else {
            for (int i = currentIdx; i < passwordLength; i++) {
                password[i] = getRandomChar(fullPool);
            }
        }

        shuffle(password);
        return password;
    }

    private char getRandomChar(String chars) {
        return chars.charAt(SECURE_RANDOM.nextInt(chars.length()));
    }

    private int countRequiredTypeChar(GeneratorProfileForPasswordDto dto) {
        int count = 0;
        if (dto.isUppercase()) ++count;
        if (dto.isLowercase()) ++count;
        if (dto.isDigits()) ++count;
        if (dto.isSpecialChars()) ++count;
        if (dto.isDuplicateChars()) ++count;
        return count;
    }

    private void shuffle(char[] sequence) {
        for (int i = sequence.length - 1; i > 0; i--) {
            int randomIdx = SECURE_RANDOM.nextInt(i + 1);
            swap(sequence, i, randomIdx);
        }
    }

    private void swap(char[] sequence, int from, int to) {
        char temp = sequence[to];
        sequence[to] = sequence[from];
        sequence[from] = temp;
    }
}
