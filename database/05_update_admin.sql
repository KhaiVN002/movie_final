-- Reset password of test1@gmail.com to 123456
UPDATE users 
SET password = '$2a$12$gWo/wJvj4Ru6/ocis6LuHO42YmbwvmredrvYnqZrN5zAdHhQtfq12' 
WHERE email = 'test1@gmail.com';

-- Ensure test1@gmail.com has the ADMIN role (role_id = 1)
INSERT INTO user_role (user_id, role_id)
SELECT id, 1
FROM users
WHERE email = 'test1@gmail.com'
ON CONFLICT (user_id, role_id) DO NOTHING;
