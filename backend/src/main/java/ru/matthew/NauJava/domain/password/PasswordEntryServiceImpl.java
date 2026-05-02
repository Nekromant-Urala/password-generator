package ru.matthew.NauJava.domain.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionService;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryCreateDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntrySpecDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.domain.password.mapper.PasswordEntryMapper;

import java.util.Arrays;
import java.util.List;

import static ru.matthew.NauJava.common.utils.ConverterUtils.charsToBytes;

@Service
@Transactional
public class PasswordEntryServiceImpl implements PasswordEntryService {

    private final PasswordEntryMapper passwordEntryMapper;
    private final PasswordEntryRepository passwordEntryRepository;
    private final EncryptionService encryptionService;

    @Autowired
    public PasswordEntryServiceImpl(PasswordEntryMapper passwordEntryMapper, PasswordEntryRepository passwordEntryRepository, EncryptionService encryptionService) {
        this.passwordEntryMapper = passwordEntryMapper;
        this.passwordEntryRepository = passwordEntryRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public PasswordEntryResponseDto createPasswordEntry(PasswordEntryCreateDto dto) {
        var entry = passwordEntryMapper.toPasswordEntry(dto);

        entry.setPassword(Arrays.toString(
                encryptionService.encrypt(
                        charsToBytes(dto.password()),
                        dto.password(),
                        dto.cipherSpec(),
                        dto.kdfSpec(),
                        dto.iterations())
        ));

        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordEntryResponseDto findById(Long id) {
        return passwordEntryRepository.findById(id).map(passwordEntryMapper::toPasswordEntryResponseDto).orElseThrow(
                () -> new PasswordEntryNotFoundException("Запись с таким id: '%d' не была найдена.".formatted(id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordEntryResponseDto> findByServiceName(String serviceName) {
        return passwordEntryRepository.findByServiceName(serviceName).stream()
                .map(passwordEntryMapper::toPasswordEntryResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordEntryResponseDto> findByUserId(Long userId) {
        return passwordEntryRepository.findByUserId(userId).stream()
                .map(passwordEntryMapper::toPasswordEntryResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordEntryResponseDto> findAll() {
        return passwordEntryRepository.findAll().stream()
                .map(passwordEntryMapper::toPasswordEntryResponseDto)
                .toList();
    }

    @Override
    public PasswordEntryResponseDto updateLogin(Long id, String login) {
        var entry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Запись с таким id: '%d' не была найдена.".formatted(id))
        );
        entry.setLogin(login);
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public PasswordEntryResponseDto updateServiceName(Long id, String serviceName) {
        var entry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Запись с таким id: '%d' не была найдена.".formatted(id))
        );
        entry.setServiceName(serviceName);
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public PasswordEntryResponseDto updateDescription(Long id, String description) {
        var entry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Запись с таким id: '%d' не была найдена.".formatted(id))
        );
        entry.setDescription(description);
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public PasswordEntryResponseDto updatePassword(Long id, PasswordEntrySpecDto dto) {
        var entry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Запись с таким id: '%d' не была найдена.".formatted(id))
        );
        entry.setPassword(Arrays.toString(
                encryptionService.encrypt(
                        charsToBytes(dto.password()),
                        dto.password(),
                        dto.cipherSpec(),
                        dto.kdfSpec(),
                        dto.iterations())
        ));
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public void deleteByServiceName(Long id, String serviceName) {
        passwordEntryRepository.deleteByServiceName(serviceName);
    }

    @Override
    public void deleteById(Long id) {
        passwordEntryRepository.deleteById(id);
    }
}
