-- CreateTable
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(191) UNIQUE NOT NULL,
    name VARCHAR(191),
    hash_salt VARCHAR(500) NOT NULL,
    roles VARCHAR(16)[]
);

-- CreateTable
CREATE TABLE IF NOT EXISTS post (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(191),
    published BOOLEAN NOT NULL DEFAULT false,
    title VARCHAR(191) NOT NULL,
    posted_at TIMESTAMP NOT NULL,
    author_id BIGINT NOT NULL
);

-- AddForeignKey
ALTER TABLE post ADD FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE;
