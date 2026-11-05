-- ---------------------------------------------------------------------------
-- spring-blog schema (MySQL 8.0+, utf8mb4)
--
-- Structure only. Demo rows live in data.sql.
-- No foreign keys on purpose: the application deletes child rows inside a single
-- transaction (ArticleServiceImpl#delete / UserServiceImpl#deleteUser), and the
-- unique indexes below are what the code actually relies on for its invariants.
-- ---------------------------------------------------------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `blog_system`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `blog_system`;

-- ---------------------------------------------------------------------------
-- Roles
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_authority`;
CREATE TABLE `t_authority` (
    `id`        int NOT NULL AUTO_INCREMENT,
    `authority` varchar(200) NOT NULL COMMENT 'Spring Security authority name',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_authority` (`authority`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Roles';

-- ---------------------------------------------------------------------------
-- Accounts
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id`             int NOT NULL AUTO_INCREMENT,
    `username`       varchar(50)  NOT NULL,
    `password`       varchar(100) NOT NULL COMMENT 'bcrypt hash',
    `email`          varchar(200) NOT NULL,
    `created`        datetime     NULL DEFAULT NULL,
    `valid`          tinyint(1)   NOT NULL DEFAULT 1 COMMENT '1 = enabled, 0 = disabled',
    `articles_count` int          NOT NULL DEFAULT 0 COMMENT 'Articles published',
    `comments_count` int          NOT NULL DEFAULT 0 COMMENT 'Comments written',
    PRIMARY KEY (`id`),
    -- Registration checks these first; the indexes also close the race between
    -- the check and the insert (DuplicateKeyException -> ConflictException).
    UNIQUE KEY `uk_user_username` (`username`),
    UNIQUE KEY `uk_user_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Accounts';

DROP TABLE IF EXISTS `t_user_authority`;
CREATE TABLE `t_user_authority` (
    `id`           int NOT NULL AUTO_INCREMENT,
    `user_id`      int NOT NULL,
    `authority_id` int NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_authority` (`user_id`, `authority_id`),
    KEY `idx_authority_id` (`authority_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Account roles';

DROP TABLE IF EXISTS `t_user_profile`;
CREATE TABLE `t_user_profile` (
    `id`       int          NOT NULL AUTO_INCREMENT,
    `user_id`  int          NOT NULL,
    `nickname` varchar(50)  NULL DEFAULT NULL,
    `avatar`   varchar(200) NULL DEFAULT NULL,
    `bio`      text         NULL,
    `website`  varchar(200) NULL DEFAULT NULL,
    `location` varchar(100) NULL DEFAULT NULL,
    `birthday` date         NULL DEFAULT NULL,
    `gender`   tinyint(1)   NULL DEFAULT NULL COMMENT '0 = female, 1 = male',
    `created`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profile_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Profile per account';

-- ---------------------------------------------------------------------------
-- Articles
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_article`;
CREATE TABLE `t_article` (
    `id`               int          NOT NULL AUTO_INCREMENT,
    `title`            varchar(50)  NOT NULL,
    `content`          longtext     NULL COMMENT 'Markdown source',
    `created`          datetime     NOT NULL,
    `modified`         datetime     NULL DEFAULT NULL,
    `categories`       varchar(200) NULL DEFAULT 'Uncategorized',
    `tags`             varchar(200) NULL DEFAULT NULL COMMENT 'Comma separated',
    `allow_comment`    tinyint(1)   NOT NULL DEFAULT 1,
    `thumbnail`        varchar(200) NULL DEFAULT NULL,
    `likes_count`      int          NOT NULL DEFAULT 0 COMMENT 'Likes',
    `favorites_count`  int          NOT NULL DEFAULT 0 COMMENT 'Favourites',
    `author`           varchar(50)  NULL DEFAULT NULL COMMENT 'Username of the author',
    `status`           varchar(20)  NOT NULL DEFAULT 'published' COMMENT 'draft | published | scheduled',
    `publish_time`     datetime     NULL DEFAULT NULL,
    `last_saved_time`  datetime     NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_article_status` (`status`),
    KEY `idx_article_created` (`created`),
    KEY `idx_article_author` (`author`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Articles';

DROP TABLE IF EXISTS `t_statistic`;
CREATE TABLE `t_statistic` (
    `id`           int NOT NULL AUTO_INCREMENT,
    `article_id`   int NOT NULL,
    `hits`         int NOT NULL DEFAULT 0,
    `comments_num` int NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_statistic_article` (`article_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Per-article counters';

DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
    `id`         int          NOT NULL AUTO_INCREMENT,
    `article_id` int          NOT NULL,
    `created`    datetime     NOT NULL,
    `ip`         varchar(200) NULL DEFAULT NULL,
    `content`    text         NOT NULL,
    `status`     varchar(20)  NOT NULL DEFAULT 'approved' COMMENT 'approved | pending | rejected',
    `author`     varchar(50)  NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_comment_article` (`article_id`),
    KEY `idx_comment_created` (`created`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Comments';

DROP TABLE IF EXISTS `t_article_like`;
CREATE TABLE `t_article_like` (
    `id`         int      NOT NULL AUTO_INCREMENT,
    `user_id`    int      NOT NULL,
    `article_id` int      NOT NULL,
    `created`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_like_user_article` (`user_id`, `article_id`),
    KEY `idx_like_article` (`article_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Likes';

DROP TABLE IF EXISTS `t_article_favorite`;
CREATE TABLE `t_article_favorite` (
    `id`         int      NOT NULL AUTO_INCREMENT,
    `user_id`    int      NOT NULL,
    `article_id` int      NOT NULL,
    `created`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_favorite_user_article` (`user_id`, `article_id`),
    KEY `idx_favorite_article` (`article_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Favourites';

-- ---------------------------------------------------------------------------
-- Counters are maintained by triggers so a like/favourite row and the denormalised
-- number on t_article can never drift apart, even if a write bypasses the service.
-- ---------------------------------------------------------------------------
DROP TRIGGER IF EXISTS `tr_article_like_insert`;
DROP TRIGGER IF EXISTS `tr_article_like_delete`;
DROP TRIGGER IF EXISTS `tr_article_favorite_insert`;
DROP TRIGGER IF EXISTS `tr_article_favorite_delete`;

DELIMITER ;;
CREATE TRIGGER `tr_article_like_insert` AFTER INSERT ON `t_article_like` FOR EACH ROW
BEGIN
    UPDATE `t_article` SET `likes_count` = `likes_count` + 1 WHERE `id` = NEW.`article_id`;
END;
;;

CREATE TRIGGER `tr_article_like_delete` AFTER DELETE ON `t_article_like` FOR EACH ROW
BEGIN
    UPDATE `t_article` SET `likes_count` = GREATEST(`likes_count` - 1, 0) WHERE `id` = OLD.`article_id`;
END;
;;

CREATE TRIGGER `tr_article_favorite_insert` AFTER INSERT ON `t_article_favorite` FOR EACH ROW
BEGIN
    UPDATE `t_article` SET `favorites_count` = `favorites_count` + 1 WHERE `id` = NEW.`article_id`;
END;
;;

CREATE TRIGGER `tr_article_favorite_delete` AFTER DELETE ON `t_article_favorite` FOR EACH ROW
BEGIN
    UPDATE `t_article` SET `favorites_count` = GREATEST(`favorites_count` - 1, 0) WHERE `id` = OLD.`article_id`;
END;
;;
DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;
