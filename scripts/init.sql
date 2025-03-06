--
-- DROP DATABASE IF EXISTS user_dev;
--
-- CREATE DATABASE user_dev
--     OWNER "wqSysadmin"
--     ENCODING 'UTF8'
--     LC_COLLATE='en_US.UTF-8'
--     LC_CTYPE='en_US.UTF-8'
--     TEMPLATE template0;
drop function update_updated_at_column;
-- 🌍 Bảng Role (Chứa các vai trò: system_admin, station_admin, users)
DROP TABLE IF EXISTS "role" CASCADE;
CREATE TABLE "role" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(50) NOT NULL UNIQUE,
    "description" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 🚀 Bảng Permission (Quyền của từng Role)
DROP TABLE IF EXISTS "permission" CASCADE;
CREATE TABLE "permission" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(100) NOT NULL UNIQUE,
    "description" TEXT NOT NULL,
    "station_id" INTEGER NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "display_name" VARCHAR(255) NOT NULL,
    "role_id" INTEGER NOT NULL  -- 🔥 Chưa có FOREIGN KEY
);

-- 🏢 Bảng Tổ Chức (Trạm Quan Trắc)
DROP TABLE IF EXISTS "org" CASCADE;
CREATE TABLE "org" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "address" VARCHAR(255),
    "city" VARCHAR(255),
    "country" VARCHAR(255),
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "station_id" INTEGER NOT NULL
);

-- 🛠 Xóa bảng nếu đã tồn tại
DROP TABLE IF EXISTS "users" CASCADE;

-- 👤 Tạo bảng Users
CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "email" VARCHAR(255) NOT NULL UNIQUE,
    "username" VARCHAR(255) UNIQUE,
    "full_name" VARCHAR(255) NOT NULL,
    "password_hash" TEXT NOT NULL, -- 🔥 Thêm cột mật khẩu mã hóa
    "email_verified" BOOLEAN DEFAULT FALSE,
    "last_seen_at" TIMESTAMPTZ DEFAULT NULL,
    "status" VARCHAR(50) NOT NULL DEFAULT 'active',
    "is_admin" BOOLEAN DEFAULT FALSE,
    "kong_consumer_id" UUID DEFAULT NULL UNIQUE, -- Mapping với Kong nếu dùng Key Auth
    CONSTRAINT chk_status CHECK (status IN ('active', 'inactive', 'banned')) -- Đảm bảo giá trị hợp lệ
);

-- 🔗 Bảng Gán User vào Role
DROP TABLE IF EXISTS "user_role" CASCADE;
CREATE TABLE "user_role" (
    "id" SERIAL PRIMARY KEY,
    "org_id" INTEGER NOT NULL, -- 🔥 Chưa có FOREIGN KEY
    "role_id" INTEGER NOT NULL, -- 🔥 Chưa có FOREIGN KEY
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "user_id" INTEGER NOT NULL -- 🔥 Chưa có FOREIGN KEY
);

-- 🔗 Bảng User thuộc Tổ Chức (org_user)
DROP TABLE IF EXISTS "org_user" CASCADE;
CREATE TABLE "org_user" (
    "id" SERIAL PRIMARY KEY,
    "org_id" INTEGER NOT NULL, -- 🔥 Chưa có FOREIGN KEY
    "user_id" INTEGER NOT NULL, -- 🔥 Chưa có FOREIGN KEY
    "role" VARCHAR(50) NOT NULL
);

-- 🛠 Bảng Lưu OAuth Provider (SSO)
DROP TABLE IF EXISTS "user_sso" CASCADE;
CREATE TABLE "user_sso" (
    "id" SERIAL PRIMARY KEY,
    "user_id" INTEGER NOT NULL, -- 🔥 Chưa có FOREIGN KEY
    "auth_provider" VARCHAR(50) NOT NULL CHECK (auth_provider IN ('google', 'github', 'microsoft')),
    "auth_id" VARCHAR(255) NOT NULL UNIQUE,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ⚙️ Bảng Cấu Hình SSO
DROP TABLE IF EXISTS "sso_setting" CASCADE;
CREATE TABLE "sso_setting" (
    "id" SERIAL PRIMARY KEY,
    "provider" VARCHAR(255) NOT NULL UNIQUE,
    "settings" TEXT NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "is_deleted" BOOLEAN NOT NULL DEFAULT FALSE
);

-- 🔒 Bảng Ghi Log Thử Đăng Nhập (Login Attempt)
DROP TABLE IF EXISTS "login_attempt" CASCADE;
CREATE TABLE "login_attempt" (
    "id" SERIAL PRIMARY KEY,
    "username" VARCHAR(255) NOT NULL,
    "ip_address" INET NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

DROP TABLE IF EXISTS "user_refresh_token" CASCADE;
CREATE TABLE "user_refresh_token" (
    "id" SERIAL PRIMARY KEY,
    "user_id" INTEGER NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
    "refresh_token" TEXT NOT NULL UNIQUE,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "expires_at" TIMESTAMPTZ NOT NULL
);

-- DROP TABLE IF EXISTS "user_auth_token" CASCADE;
-- CREATE TABLE "user_auth_token" (
--     "id" SERIAL NOT NULL UNIQUE,
--     "user_id" INTEGER NOT NULL,
--     "auth_token" TEXT NOT NULL,             -- Access token
--     "prev_auth_token" TEXT NOT NULL,        -- Token cũ nếu có
--     "client_ip" VARCHAR(255) NOT NULL,      -- IP client đăng nhập
--     "auth_token_seen" INTEGER NOT NULL,     -- Số lần nhìn thấy token
--     "seen_at" TIMESTAMP,                    -- Thời gian lần cuối token được nhìn thấy
--     "rotated_at" TIMESTAMP NOT NULL,        -- Thời gian token bị thay thế
--     "updated_at" TIMESTAMP NOT NULL,        -- Thời gian cập nhật token
--     "revoked_at" TIMESTAMP,                 -- Thời gian revoke token
--     "external_session_id" INTEGER,          -- ID của session từ hệ thống bên ngoài (ví dụ: OAuth2)
--     PRIMARY KEY("id")
-- );

-- DROP TABLE IF EXISTS "user_external_session" CASCADE;
-- CREATE TABLE "user_external_session" (
--     "id" SERIAL NOT NULL UNIQUE,
--     "user_auth_id" INTEGER NOT NULL,  -- Liên kết với user_auth_token
--     "user_id" INTEGER NOT NULL,       -- Liên kết với user
--     "id_token" TEXT,                  -- OAuth2 ID token
--     "refresh_token" TEXT,             -- Refresh token
--     "session_id" TEXT,                -- Session ID
--     "session_id_hash" TEXT,           -- Hash của session ID để bảo mật
--     "name_id" TEXT,                   -- ID của người dùng từ OAuth2
--     "expires_at" TIMESTAMP,           -- Thời gian hết hạn của refresh token
--     "created_at" TIMESTAMP NOT NULL,  -- Thời gian tạo session
--     PRIMARY KEY("id")
-- );

-- 🛠 Function Auto-update updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 📌 Tạo Triggers để Auto-update updated_at
CREATE TRIGGER trg_update_user_updated_at
BEFORE UPDATE ON "users"
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER trg_update_role_updated_at
BEFORE UPDATE ON "role"
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER trg_update_permission_updated_at
BEFORE UPDATE ON "permission"
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER trg_update_org_updated_at
BEFORE UPDATE ON "org"
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER trg_update_user_role_updated_at
BEFORE UPDATE ON "user_role"
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER trg_update_org_user_updated_at
BEFORE UPDATE ON "org_user"
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();


-- 🌍 Thêm các vai trò (roles)
INSERT INTO "role" ("name", "description", "created_at") VALUES
('system_admin', 'Quản trị viên hệ thống, có toàn quyền', NOW()),
('station_admin', 'Admin của một trạm quan trắc, có quyền quản lý dữ liệu của trạm đó', NOW()),
('users', 'Người dùng thông thường, chỉ có quyền xem dữ liệu và quản lý thông báo cá nhân', NOW());

-- 🚀 Thêm quyền hạn (permissions)
INSERT INTO "permission" ("name", "description", "station_id", "created_at", "updated_at", "display_name", "role_id") VALUES
-- System Admin
('manage_all', 'Quản lý toàn bộ hệ thống', 0, NOW(), NOW(), 'Quản lý Hệ thống', 1),
('view_all_reports', 'Xem toàn bộ báo cáo trên hệ thống', 0, NOW(), NOW(), 'Xem Tất cả Báo cáo', 1),

-- Station Admin
('upload_station_data', 'Upload dữ liệu của trạm quan trắc', 1, NOW(), NOW(), 'Tải lên dữ liệu trạm', 2),
('manage_station_users', 'Quản lý người dùng trong trạm', 1, NOW(), NOW(), 'Quản lý User Trạm', 2),
('generate_reports', 'Tạo báo cáo chất lượng nước', 1, NOW(), NOW(), 'Tạo Báo cáo', 2),

-- User thường
('manage_alerts', 'Tạo và quản lý cảnh báo cá nhân', 0, NOW(), NOW(), 'Quản lý Cảnh báo', 3),
('schedule_reports', 'Lên lịch báo cáo cá nhân', 0, NOW(), NOW(), 'Lên Lịch Báo cáo', 3);

INSERT INTO "org" (id, name, address, city, country, created_at, updated_at, station_id)
VALUES (0, 'No Organization', NULL, NULL, NULL, NOW(), NOW(), 0);

-- 🏢 Thêm trạm quan trắc nước (org)
INSERT INTO "org" ("name", "address", "city", "country", "created_at", "updated_at", "station_id") VALUES
('Trạm Quan Trắc Mekong', '123 Mekong St', 'Can Tho', 'Vietnam', NOW(), NOW(), 1),
('Trạm Quan Trắc Sai Gon', '456 Sai Gon St', 'Ho Chi Minh', 'Vietnam', NOW(), NOW(), 2),
('Trạm Quan Trắc Hanoi', '789 Red River', 'Hanoi', 'Vietnam', NOW(), NOW(), 3);

-- 🔍 Tạo index trên email để tăng tốc truy vấn
CREATE INDEX idx_users_email ON "users" (email);

-- 📌 Insert System Admin (có toàn quyền)
INSERT INTO "users" ("email", "username", "full_name", "password_hash", "email_verified", "status", "is_admin", "kong_consumer_id", "created_at", "updated_at") VALUES
('admin@wqsys.com', 'sysadmin', 'System Admin', '$2a$10$ABCXYZ... (hashed)', TRUE, 'active', TRUE, '11111111-1111-1111-1111-111111111111', NOW(), NOW());

-- 📌 Insert Admin các trạm quan trắc
INSERT INTO "users" ("email", "username", "full_name", "password_hash", "email_verified", "status", "is_admin", "kong_consumer_id", "created_at", "updated_at") VALUES
('mekong_admin@wqsys.com', 'mekong_admin', 'Mekong Station Admin', '$2a$10$DEFXYZ...', TRUE, 'active', FALSE, '22222222-2222-2222-2222-222222222222', NOW(), NOW()),
('saigon_admin@wqsys.com', 'saigon_admin', 'Saigon Station Admin', '$2a$10$GHIXYZ...', TRUE, 'active', FALSE, '33333333-3333-3333-3333-333333333333', NOW(), NOW()),
('hanoi_admin@wqsys.com', 'hanoi_admin', 'Hanoi Station Admin', '$2a$10$JKLXYZ...', TRUE, 'active', FALSE, '44444444-4444-4444-4444-444444444444', NOW(), NOW());

-- 📌 Insert User bình thường
INSERT INTO "users" ("email", "username", "full_name", "password_hash", "email_verified", "status", "is_admin", "kong_consumer_id", "created_at", "updated_at") VALUES
('user1@wqsys.com', 'user1', 'User One', '$2a$10$MNOXYZ...', TRUE, 'active', FALSE, '55555555-5555-5555-5555-555555555555', NOW(), NOW()),
('user2@wqsys.com', 'user2', 'User Two', '$2a$10$PQRXYZ...', TRUE, 'active', FALSE, '66666666-6666-6666-6666-666666666666', NOW(), NOW());

-- -- 📌 Insert thêm user test
-- INSERT INTO "users" ("email", "username", "full_name", "password_hash", "email_verified", "status", "is_admin", "kong_consumer_id", "created_at", "updated_at") VALUES
-- ('user1@example.com', 'user1example', 'User One', '$2a$10$STUXYZ...', TRUE, 'active', FALSE, '01ede1e7-fdb4-4a67-bc67-a17e051f86d6', NOW(), NOW());

-- 🔑 Gán users vào tổ chức (org_user)
INSERT INTO "org_user" ("org_id", "user_id", "role") VALUES
-- System Admin (org = 0 nhưng có quyền tất cả)
(0, 1, 'system_admin'),

-- Admin từng trạm
(1, 2, 'station_admin'),
(2, 3, 'station_admin'),
(3, 4, 'station_admin'),

-- User bình thường (org = 0)
(0, 5, 'users'),
(0, 6, 'users');

-- 🛠 Gán role cho users (user_role)
INSERT INTO "user_role" ("org_id", "role_id", "created_at", "user_id") VALUES
-- System Admin (có toàn quyền)
(0, 1, NOW(), 1),

-- Admin từng trạm
(1, 2, NOW(), 2),
(2, 2, NOW(), 3),
(3, 2, NOW(), 4),

-- User bình thường
(0, 3, NOW(), 5),
(0, 3, NOW(), 6);

-- 🔗 Mapping users SSO (nếu users dùng OAuth Google)
INSERT INTO "user_sso" ("user_id", "auth_provider", "auth_id", "created_at") VALUES
-- User1 & User2 đăng nhập bằng Google OAuth
-- (5, 'google', 'google-oauth-user1@wqsys.com', NOW()),
(6, 'google', 'google-oauth-user2@wqsys.com', NOW());

INSERT INTO "user_sso" ("user_id", "auth_provider", "auth_id", "created_at")
VALUES (5, 'google', 'google-oauth-user1@example.com', NOW());

-- ⚙️ Cấu hình Google SSO
INSERT INTO "sso_setting" ("provider", "settings", "created_at", "updated_at", "is_deleted") VALUES
('google', '{"client_id": "759159252498-1tu1ben7amd25d8dfm2kljd3u05683i3.apps.googleusercontent.com", "client_secret": "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", "redirect_uri": "https://example.com/auth/callback"}', NOW(), NOW(), FALSE);

-- 🔗 Liên kết permission với role
ALTER TABLE "permission"
ADD CONSTRAINT fk_permission_role FOREIGN KEY ("role_id") REFERENCES "role"("id") ON DELETE CASCADE;

-- 🔗 Liên kết user_role với users, role, org
ALTER TABLE "user_role"
ADD CONSTRAINT fk_user_role_user FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE,
ADD CONSTRAINT fk_user_role_role FOREIGN KEY ("role_id") REFERENCES "role"("id") ON DELETE CASCADE,
ADD CONSTRAINT fk_user_role_org FOREIGN KEY ("org_id") REFERENCES "org"("id") ON DELETE CASCADE;

-- 🔗 Liên kết org_user với users và org
ALTER TABLE "org_user"
ADD CONSTRAINT fk_org_user_user FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE,
ADD CONSTRAINT fk_org_user_org FOREIGN KEY ("org_id") REFERENCES "org"("id") ON DELETE CASCADE;

-- 🔗 Liên kết user_sso với users
ALTER TABLE "user_sso"
ADD CONSTRAINT fk_user_sso_user FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE;

-- 🔗 Liên kết login_attempt với users (nếu muốn log ai đã thử đăng nhập)
ALTER TABLE "login_attempt"
ADD CONSTRAINT fk_login_attempt_user FOREIGN KEY ("username") REFERENCES "users"("username") ON DELETE CASCADE;