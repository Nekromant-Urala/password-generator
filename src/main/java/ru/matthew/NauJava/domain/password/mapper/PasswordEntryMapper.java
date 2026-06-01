package ru.matthew.NauJava.domain.password.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto;

import java.nio.CharBuffer;

@Component
public class PasswordEntryMapper {

    public PasswordEntry toPasswordEntry(PasswordEntryRequestDto dto) {
        if (dto == null) {
            return null;
        }
        PasswordEntry entry = new PasswordEntry();
        entry.setLogin(dto.login());
        entry.setServiceName(dto.serviceName());
        entry.setDescription(dto.description());
        return entry;
    }

    public PasswordEntry toPasswordEntry(PasswordEntryUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        PasswordEntry entry = new PasswordEntry();
        entry.setLogin(dto.login());
        entry.setPassword(CharBuffer.wrap(dto.password()).toString());
        entry.setServiceName(dto.serviceName());
        entry.setDescription(dto.description());
        return entry;
    }

    public PasswordEntry toPasswordEntry(PasswordEntry entry, PasswordEntryUpdateDto dto) {
        if (dto == null || entry == null) {
            return null;
        }
        entry.setLogin(dto.login());
        entry.setServiceName(dto.serviceName());
        entry.setDescription(dto.description());
        return entry;
    }


    public PasswordEntryResponseDto toPasswordEntryResponseDto(PasswordEntry entry) {
        if (entry == null) {
            return null;
        }
        return new PasswordEntryResponseDto(
                entry.getId(),
                entry.getLogin(),
                entry.getPassword().toCharArray(),
                entry.getServiceName(),
                entry.getDescription(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }

    public PasswordEntryResponseDto toPasswordEntryResponseDto(PasswordEntry entry, char[] pass) {
        if (entry == null) {
            return null;
        }
        return new PasswordEntryResponseDto(
                entry.getId(),
                entry.getLogin(),
                pass,
                entry.getServiceName(),
                entry.getDescription(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
