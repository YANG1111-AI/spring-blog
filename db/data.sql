-- ---------------------------------------------------------------------------
-- spring-blog demo data
--
-- Everything here is fake on purpose. The two accounts use passwords that are
-- documented in README.md, so change or delete them before exposing the site.
--
--   admin / admin123456  -> ROLE_ADMIN
--   demo  / demo123456   -> ROLE_USER
-- ---------------------------------------------------------------------------

SET NAMES utf8mb4;
USE `blog_system`;

INSERT INTO `t_authority` (`id`, `authority`) VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER');

INSERT INTO `t_user` (`id`, `username`, `password`, `email`, `created`, `valid`, `articles_count`, `comments_count`) VALUES
    (1, 'admin', '$2a$10$cSSZfQRan.PadYfIm.pzmOCwmXyKKtogdw8NR/EwD8cNfWFHFquLS', 'admin@example.com', '2026-01-01 10:00:00', 1, 3, 1),
    (2, 'demo',  '$2a$10$2V1c6U4busRWt0yOpZ9unecH0uMIoMxsRo81bpjpMZVLol.JCtG.m', 'demo@example.com',  '2026-01-02 10:00:00', 1, 0, 1);

INSERT INTO `t_user_authority` (`id`, `user_id`, `authority_id`) VALUES
    (1, 1, 1),
    (2, 2, 2);

INSERT INTO `t_user_profile` (`user_id`, `nickname`, `bio`, `website`, `location`, `gender`) VALUES
    (1, 'Admin', 'Maintains this demo blog.', 'https://example.com', 'Earth', 1),
    (2, 'Demo User', 'Just here to try the comment and like buttons.', NULL, NULL, NULL);

INSERT INTO `t_article` (`id`, `title`, `content`, `created`, `modified`, `categories`, `tags`, `allow_comment`, `thumbnail`, `likes_count`, `favorites_count`, `author`, `status`, `publish_time`, `last_saved_time`) VALUES
(1, 'Hello, Markdown',
'This post exists to show how the editor renders **Markdown**, and what happens to anything that looks like an attack.

## What the renderer supports

- Headings, lists and quotes
- Fenced code blocks

```java
@GetMapping("/article/{id}")
public String detail(@PathVariable Integer id, Model model) {
    model.addAttribute("article", articleService.openForReading(id));
    return "client/articleDetails";
}
```

- Tables (GitHub flavoured Markdown)

| Feature        | Handled by                       |
| -------------- | -------------------------------- |
| Markdown       | CommonMark                       |
| HTML filtering | OWASP java-html-sanitizer        |
| Output         | Thymeleaf `th:utext`             |

## What gets stripped

The next line is a raw `<script>` tag in the stored Markdown. It never reaches the browser as a script, because the HTML produced by CommonMark is sanitised before it is rendered:

<script>alert('xss')</script>

Images with a `javascript:` source are removed as well: ![x](javascript:alert(1)).

`<a href="/article/1" onclick="alert(1)">links like this</a>` lose the event handler too.',
'2026-01-03 09:00:00', NULL, 'Guides', 'markdown,security', 1, NULL, 0, 0, 'admin', 'published', NULL, NULL),

(2, 'Why this project uses Spring Boot 3.3',
'Spring Boot 2.7 reached the end of open-source support, so the upgrade was not cosmetic:

- `javax.*` imports became `jakarta.*`
- `WebSecurityConfigurerAdapter` was removed; security is now a `SecurityFilterChain` bean
- Spring Security 6 enables CSRF by default, so the templates had to send the token

The database layer is intentionally plain MyBatis: the SQL is visible in `src/main/resources/mapper`, which makes the query cost of every page easy to reason about.',
'2026-01-04 09:00:00', NULL, 'Notes', 'spring-boot,migration', 1, NULL, 0, 0, 'admin', 'published', NULL, NULL),

(3, 'Draft: notes on access control',
'This row is a draft, so it shows up in the admin list but not on the public pages.',
'2026-01-05 09:00:00', NULL, 'Notes', 'draft', 1, NULL, 0, 0, 'admin', 'draft', NULL, NULL);

INSERT INTO `t_statistic` (`article_id`, `hits`, `comments_num`) VALUES
    (1, 12, 1),
    (2, 5, 1),
    (3, 0, 0);

INSERT INTO `t_comment` (`id`, `article_id`, `created`, `ip`, `content`, `status`, `author`) VALUES
    (1, 1, '2026-01-03 12:00:00', '0:0:0:0:0:0:0:1', 'The table renders correctly.', 'approved', 'demo'),
    (2, 2, '2026-01-04 12:00:00', '0:0:0:0:0:0:0:1', 'Jakarta migration is the part everyone forgets.', 'approved', 'demo');

-- The insert triggers bump likes_count / favorites_count on t_article.
INSERT INTO `t_article_like` (`user_id`, `article_id`, `created`) VALUES
    (2, 1, '2026-01-03 13:00:00');

INSERT INTO `t_article_favorite` (`user_id`, `article_id`, `created`) VALUES
    (2, 2, '2026-01-04 13:00:00');
