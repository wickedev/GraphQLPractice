-- CreateTable
CREATE TABLE IF NOT EXISTS user (
    email VARCHAR(191) NOT NULL,
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(191),
    UNIQUE INDEX user.email_unique(email),

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE IF NOT EXISTS post (
    author_id BIGINT,
    content VARCHAR(191),
    id BIGINT NOT NULL AUTO_INCREMENT,
    published BOOLEAN NOT NULL DEFAULT false,
    title VARCHAR(191) NOT NULL,
    posted_at DATETIME NOT NULL,

    PRIMARY KEY (id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE post ADD FOREIGN KEY (author_id) REFERENCES user(id) ON DELETE SET NULL ON UPDATE CASCADE;
