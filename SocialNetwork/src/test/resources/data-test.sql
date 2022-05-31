INSERT INTO roles (name) values
                             ('ROLE_ADMIN'),
                             ('ROLE_APPUSER');

INSERT INTO users (id, email, password, name, surname, username, status) values
    ('ccb953a7-d244-48bb-8627-4b2437491dc1', 'mateja.test@vegait.rs', '$2a$12$aBK0PhtcZ9Zg.gBHCFh6Lu9d.oAypyEfhPtUevhQllLC1CD2Wtxku', 'Admin', 'Adminkovic', 'admin', 'ACTIVATED'),
    ('ccb953a7-d244-48bb-8627-4b2437491dc2', 'mateja.test1@vegait.rs', '$2a$12$aBK0PhtcZ9Zg.gBHCFh6Lu9d.oAypyEfhPtUevhQllLC1CD2Wtxku', 'Admin', 'Adminkovic', 'maka', 'ACTIVATED');

INSERT INTO user_role (user_id, role_id) values
    ('ccb953a7-d244-48bb-8627-4b2437491dc1', 1),
    ('ccb953a7-d244-48bb-8627-4b2437491dc2', 1);
