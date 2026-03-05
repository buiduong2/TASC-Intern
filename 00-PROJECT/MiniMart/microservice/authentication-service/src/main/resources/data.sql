INSERT INTO "role" (id, description, name) VALUES
(1, 'Full system access, can manage users, products, and orders.', 'ADMIN'),
(2, 'Responsible for daily operations such as managing products and handling orders.', 'STAFF'),
(3, 'Regular user with the ability to browse products, place orders, and manage personal account.', 'CUSTOMER');

-- 1) Insert base User row (JOINED inheritance -> 1 row in users)
INSERT INTO users (id, full_name, status, avatar_url, created_at, updated_at, user_type)
VALUES (100, 'Root Admin', 'ACTIVE', NULL, NOW(), NOW(), 'SYS');

-- 2) Insert subclass row in system_users (same id)
INSERT INTO system_users (id, username, password, email, token_version)
VALUES (
  100,
  'ROOT_USER',
  '$2a$12$N5wxnamhumc3vYsTanX2U.L2FGOD2CkarMJpfzDJjmjEQLqy6St8S',
  'root@minimart.local',
  0
);

-- 3) Assign ADMIN role (role id = 1 from your seed)
INSERT INTO users_roles (user_id, roles_id) VALUES (100, 1);