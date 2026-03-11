package ru.matthew.NauJava.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.matthew.NauJava.service.PasswordEntryService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.matthew.NauJava.cli.Command.*;

@Component
public class CommandProcessor {
    private final PasswordEntryService passwordEntryService;
    private static final Map<String, Command> COMMAND_MAP = new HashMap<>(values().length);

    static {
        Arrays.stream(values())
                .filter(e -> !Objects.equals(e, NO_COMMAND))
                .forEach(command -> COMMAND_MAP.put(command.getName(), command));
    }

    @Autowired
    public CommandProcessor(PasswordEntryService passwordEntryService) {
        this.passwordEntryService = passwordEntryService;
    }

    public void processCommand(String input) {
        String[] cmd = input.split(" ");

        Command command = COMMAND_MAP.getOrDefault(cmd[0], NO_COMMAND);

        switch (command) {
            case CREATE -> {
                try {
                    passwordEntryService.createPasswordEntry(
                            Long.valueOf(cmd[1]),
                            cmd[2],
                            cmd[3],
                            cmd[4],
                            cmd[5]
                    );
                    System.out.println("Запись успешно добавлена...");
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }
            }
            case FIND_BY_ID -> {
                var passwordEntry = passwordEntryService.findById(Long.valueOf(cmd[1]));
                if (passwordEntry != null) {
                    System.out.println("Запись успешно найдена");
                    System.out.println(passwordEntry);
                } else {
                    System.out.println("Записи с таким ID нет");
                }
            }
            case FIND_BY_SERVICE_NAME -> {
                var passwordEntry = passwordEntryService.findByServiceName(cmd[1]);
                if (!passwordEntry.isEmpty()) {
                    System.out.println("Записи успешно найдены");
                    System.out.println(passwordEntry);
                } else {
                    System.out.println("Записи с таким ServiceName нет");
                }
            }
            case FIND_ALL -> {
                var passwordEntry = passwordEntryService.findAll();
                if (!passwordEntry.isEmpty()) {
                    System.out.println("Записи успешно найдены");
                    System.out.println(passwordEntry);
                } else {
                    System.out.println("Записей нет");
                }
            }
            case UPDATE_LOGIN -> {
                passwordEntryService.updateLogin(Long.valueOf(cmd[1]), cmd[2]);
                System.out.printf("Логин в записи с id=%s был обновлен\n", cmd[1]);
            }
            case UPDATE_PASSWORD -> {
                passwordEntryService.updatePassword(Long.valueOf(cmd[1]), cmd[2]);
                System.out.printf("Пароль в записи с id=%s был обновлен\n", cmd[1]);
            }
            case UPDATE_DESCRIPTION -> {
                passwordEntryService.updateDescription(Long.valueOf(cmd[1]), cmd[2]);
                System.out.printf("Описание в записи с id=%s было обновлено\n", cmd[1]);
            }
            case UPDATE_SERVICE_NAME -> {
                passwordEntryService.updateServiceName(Long.valueOf(cmd[1]), cmd[2]);
                System.out.printf("Наименование сервиса в записи с id=%s был обновлен\n", cmd[1]);
            }
            case DELETE_BY_SERVICE_NAME -> {
                passwordEntryService.deleteByServiceName(cmd[1]);
                System.out.printf("Записи содержащие наименование сервиса: \"%s\" были удалены\n", cmd[1]);
            }
            case DELETE_BY_ID -> {
                passwordEntryService.deleteById(Long.valueOf(cmd[1]));
                System.out.printf("Запись с id=%s была удалена\n", cmd[1]);
            }
            default -> {
                System.out.println("Введена неизвестная команда...");
            }
        }
    }
}
