package ru.matthew.NauJava.cli;

public enum Command {
    CREATE("create"),
    FIND_BY_ID("findById"),
    FIND_BY_SERVICE_NAME("findByServiceName"),
    FIND_ALL("findAll"),
    UPDATE_PASSWORD("updatePassword"),
    UPDATE_LOGIN("updateLogin"),
    UPDATE_SERVICE_NAME("updateServiceName"),
    UPDATE_DESCRIPTION("updateDescrpt"),
    DELETE_BY_ID("delete"),
    DELETE_BY_SERVICE_NAME("deleteByServiceName"),
    NO_COMMAND("noCommand"),
    EXIT("exit");

    private final String name;

    Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
