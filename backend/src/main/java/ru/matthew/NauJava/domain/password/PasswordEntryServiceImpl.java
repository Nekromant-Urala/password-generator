package ru.matthew.NauJava.domain.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionService;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryCreateDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntrySpecDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryDecodeException;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.domain.password.mapper.PasswordEntryMapper;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.nio.charset.CharacterCodingException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.matthew.NauJava.common.utils.ConverterUtils.bytesToString;
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

        try {
            entry.setPassword(bytesToString(
                    encryptionService.encrypt(
                            charsToBytes(dto.password()),
                            dto.password(),
                            dto.cipherSpec(),
                            dto.kdfSpec(),
                            dto.iterations())
            ));

            passwordEntryRepository.save(entry);
            return passwordEntryMapper.toPasswordEntryResponseDto(entry);
        } catch (CharacterCodingException e) {
            throw new PasswordEntryDecodeException("Ошибка при записи пароля.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordEntryResponseDto findById(Long id) {
        return passwordEntryRepository.findById(id).map(passwordEntryMapper::toPasswordEntryResponseDto).orElseThrow(
                () -> new PasswordEntryNotFoundException(id)
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
    public List<PasswordEntryResponseDto> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return passwordEntryRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(passwordEntryMapper::toPasswordEntryResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordEntryResponseDto> findByCreatedAt(LocalDateTime createdAt) {
        return passwordEntryRepository.findByCreatedAt(createdAt).stream()
                .map(passwordEntryMapper::toPasswordEntryResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordEntryResponseDto> findByUpdatedAt(LocalDateTime updatedAt) {
        return passwordEntryRepository.findByUpdatedAt(updatedAt).stream()
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

    @Override //TODO переделать связь id
    public PasswordEntryResponseDto updateLogin(Long userId, String login) {
        var entry = passwordEntryRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        entry.setLogin(login);
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public PasswordEntryResponseDto updateServiceName(Long userId, String serviceName) {
        var entry = passwordEntryRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        entry.setServiceName(serviceName);
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public PasswordEntryResponseDto updateDescription(Long userId, String description) {
        var entry = passwordEntryRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        entry.setDescription(description);
        passwordEntryRepository.save(entry);
        return passwordEntryMapper.toPasswordEntryResponseDto(entry);
    }

    @Override
    public PasswordEntryResponseDto updatePassword(Long userId, PasswordEntrySpecDto dto) {
        var entry = passwordEntryRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        try {
            entry.setPassword(bytesToString(
                    encryptionService.encrypt(
                            charsToBytes(dto.password()),
                            dto.password(),
                            dto.cipherSpec(),
                            dto.kdfSpec(),
                            dto.iterations())
            ));

            passwordEntryRepository.save(entry);
            return passwordEntryMapper.toPasswordEntryResponseDto(entry);
        } catch (CharacterCodingException e) {
            throw new PasswordEntryDecodeException("Ошибка при обновлении пароля.", e);
        }
    }

    @Override
    public void deleteByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        passwordEntryRepository.deleteByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public void deleteByUserId(Long userId) {
        passwordEntryRepository.deleteByUserId(userId);
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
