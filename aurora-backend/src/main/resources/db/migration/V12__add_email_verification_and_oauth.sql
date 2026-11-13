ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN verification_token VARCHAR(255) NULL;
ALTER TABLE users ADD COLUMN verification_token_expires_at TIMESTAMP NULL;
ALTER TABLE users ADD COLUMN auth_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL';

-- Keep existing seeded users verified so dev/test accounts continue working
UPDATE users SET email_verified = TRUE;
