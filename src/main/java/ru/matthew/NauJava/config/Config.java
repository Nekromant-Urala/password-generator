package ru.matthew.NauJava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.matthew.NauJava.cli.CommandProcessor;
import ru.matthew.NauJava.entity.PasswordEntry;

import java.util.HashMap;
import java.util.Scanner;

import static ru.matthew.NauJava.cli.Command.EXIT;

@Configuration
public class Config {
    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String version;

    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public HashMap<Long, PasswordEntry> passwordEntries() {
        return new HashMap<Long, PasswordEntry>();
    }

    @Bean
    public CommandLineRunner commandScanner(@Autowired CommandProcessor commandProcessor, @Autowired ApplicationContext ctx) {
        return args -> {
            try (var sc = new Scanner(System.in)) {
                System.out.println("Введите команду 'exit', чтобы выйти.");
                while (true) {
                    System.out.print(">");
                    var input = sc.nextLine();
                    if (EXIT.getName().equalsIgnoreCase(input.trim())) {
                        System.out.println("Выход из программы...");

                        // добавил, чтобы полностью остановить приложение (из-за web-starter`a не останавливалась работа)
                        // web-starter добавил для того, чтобы наблюдать за мониторингом spring-actuator
                        // так как через jmx (jconsole) не отображаются метрики
                        int exitCode = SpringApplication.exit(ctx, () -> 0);
                        System.exit(exitCode);
                        break;
                    }
                    commandProcessor.processCommand(input);
                }
            }
        };
    }

    public String getVersion() {
        return version;
    }

    public String getAppName() {
        return appName;
    }
}
