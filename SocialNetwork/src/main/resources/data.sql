INSERT INTO roles (name) values
             ('ROLE_ADMIN'),
             ('ROLE_APP_USER');

INSERT INTO users (id, version, email, password, name, surname, username) values
        ('ccb953a7-d244-48bb-8627-4b2437491dc1', 0, 'admin@gmail.com', '$2a$12$aBK0PhtcZ9Zg.gBHCFh6Lu9d.oAypyEfhPtUevhQllLC1CD2Wtxku', 'Admin', 'Adminkovic', 'admin');

INSERT INTO user_role (user_id, role_id) values
                  ('ccb953a7-d244-48bb-8627-4b2437491dc1', 1);
