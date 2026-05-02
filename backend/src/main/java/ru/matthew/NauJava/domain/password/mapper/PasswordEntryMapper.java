package ru.matthew.NauJava.domain.password.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryCreateDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;

@Component
public class PasswordEntryMapper {

    public PasswordEntry toPasswordEntry(PasswordEntryCreateDto dto) {
        if (dto == null) {
            return null;
        }
        PasswordEntry entry = new PasswordEntry();
        entry.setLogin(dto.login());
        entry.setServiceName(dto.serviceName());
        entry.setDescription(dto.description());
        entry.setUser(dto.user());
        return entry;
    }

    public PasswordEntryResponseDto toPasswordEntryResponseDto(PasswordEntry entry) {
        if (entry == null) {
            return null;
        }
        return new PasswordEntryResponseDto(
                entry.getId(),
                entry.getLogin(),
                entry.getPassword(),
                entry.getServiceName(),
                entry.getDescription(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
