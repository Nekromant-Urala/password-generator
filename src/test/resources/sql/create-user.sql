INSERT
INTO users (username, email, password_hash, created_at, role)
VALUES ('testName1', 'nau@email.ru', '$2a$10$uH2UdonZcguWOB/Ls/hWT.GiJT6fyijjIWml.rf8wAaIKxovVrBaC', LOCALTIMESTAMP, 'ADMIN'),
       ('testName2', 'nau@email.ru', '$2a$10$uH2UdonZcguWOB/Ls/hWT.GiJT6fyijjIWml.rf8wAaIKxovVrBaC', LOCALTIMESTAMP - INTERVAL '2 day', 'USER'),
       ('testName3', 'nau@email.ru', '$2a$10$uH2UdonZcguWOB/Ls/hWT.GiJT6fyijjIWml.rf8wAaIKxovVrBaC', LOCALTIMESTAMP + INTERVAL '2 day', 'USER');

INSERT
INTO passwords(id, login, encrypt_password, service_name, description, created_at, updated_at, user_id)
VALUES (1, 'testLogin', 'VERY HARD PASSWORD', 'testService', 'descript', LOCALTIMESTAMP, LOCALTIMESTAMP, 1),
       (2, 'testLogin', 'VERY HARD PASSWORD', 'testService', 'descript', LOCALTIMESTAMP - INTERVAL '2 day', LOCALTIMESTAMP,
        2),
       (3, 'testLogin', 'VERY HARD PASSWORD', 'testService', 'descript', LOCALTIMESTAMP + INTERVAL '2 day', LOCALTIMESTAMP,
        3);