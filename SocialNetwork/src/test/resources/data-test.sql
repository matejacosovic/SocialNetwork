INSERT INTO roles (name) values
                             ('ROLE_ADMIN'),
                             ('ROLE_APP_USER');

INSERT INTO users (id, email, password, name, surname, username, status, tenant_id) values
    ('test-id', 'mateja.test@vegait.rs', '$2a$12$.yaNGFMfd9ueDqT3LArDwOj6V0Ody4fMlteBIrYgJni0UnCx2gHfS', 'Admin', 'Adminkovic', 'admin', 'ACTIVATED', 'tenant1'),
    ('user-id', 'mateja.test2@vegait.rs', '$2a$12$.yaNGFMfd9ueDqT3LArDwOj6V0Ody4fMlteBIrYgJni0UnCx2gHfS', 'Admin', 'Adminkovic', 'user', 'ACTIVATED', 'tenant1'),
    ('ccb953a7-d244-48bb-8627-4b2437491dc2', 'mateja.test1@vegait.rs', '$2a$12$.yaNGFMfd9ueDqT3LArDwOj6V0Ody4fMlteBIrYgJni0UnCx2gHfS', 'Admin', 'Adminkovic', 'maka', 'DEACTIVATED', 'tenant1');

INSERT INTO user_role (user_id, role_id) values
    ('test-id', 1),
    ('user-id', 2),
    ('ccb953a7-d244-48bb-8627-4b2437491dc2', 1);

insert into post (id, created_date, text, image, status, user_id, tenant_id) values
    ('test-post1', '2022-05-31 10:59:11.220','test text1', 'test img1', 'VISIBLE', 'test-id', 'tenant1'),
    ('test-post2', '2022-05-30 10:59:11.220','test text2', 'test img2', 'VISIBLE', 'test-id', 'tenant1'),
    ('test-post3', '2022-05-29 10:59:11.220','test text3', 'test img3', 'VISIBLE', 'ccb953a7-d244-48bb-8627-4b2437491dc2', 'tenant1');

insert into password_reset_token (id, expiry_date, token, user_id, tenant_id) values
    (1, '2023-12-12 12:12:12', 'valid', 'test-id', 'tenant1'),
    (2, '2021-12-12 12:12:12', 'expired', 'test-id', 'tenant1');