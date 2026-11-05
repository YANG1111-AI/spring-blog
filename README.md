# spring-blog

一个基于 **Spring Boot 3.3 + Java 17** 的个人博客系统：前台阅读、Markdown 写作、评论 / 点赞 / 收藏、后台管理。

这个仓库的定位是工程实践展示，所以除了"能跑"，README 里也会说明**每个安全边界和分层决策为什么这么做、代码在哪、怎么手动验证**。

## 技术栈

| 层次 | 选型 |
| --- | --- |
| 运行时 | Java 17、Spring Boot 3.3.5 |
| Web | Spring MVC + Thymeleaf（服务端渲染） |
| 安全 | Spring Security 6（表单登录、CSRF、RBAC） |
| 持久层 | MyBatis 3 + PageHelper，MySQL 8 |
| 内容渲染 | CommonMark 0.22 + OWASP java-html-sanitizer |
| 缓存 | Spring Cache + Caffeine（可切换 Redis） |
| 构建 | Maven |

## 功能

- **前台**：首页分页、文章详情、按点击量排序的热门文章、评论、点赞、收藏、个人主页
- **写作**：发布 / 编辑 / 删除自己的文章，Markdown 编辑器，摘要自动生成
- **后台**（`/admin`，需要 `ROLE_ADMIN`）：文章、评论、用户、分类、站点统计
- **账号**：注册（自动授予 `ROLE_USER`）、登录、退出、启用 / 禁用

## 快速开始

前置条件：JDK 17+、Maven 3.8+、MySQL 8。

### 1. 初始化数据库

```bash
mysql -u root -p < db/schema.sql   # 建库、建表、建索引与触发器
mysql -u root -p < db/data.sql     # 演示数据
```

### 2. 配置连接

默认连接 `localhost:3306/blog_system`，用户 `root`、空密码。用环境变量覆盖即可，不需要改代码：

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/blog_system?...` | JDBC 地址 |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / 空 | 数据库账号 |
| `SERVER_PORT` | `8010` | 监听端口 |
| `BLOG_SITE_URL` | `http://localhost:8010` | 站点根地址（用于文章永久链接） |
| `CACHE_TYPE` | `caffeine` | 设为 `redis` 后同一套 `@Cacheable` 走 Redis |

### 3. 启动

```bash
mvn spring-boot:run
```

打开 <http://localhost:8010>。

### 演示账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `admin123456` | `ROLE_ADMIN` |
| `demo` | `demo123456` | `ROLE_USER` |

数据全部是假的，密码在 `db/data.sql` 里是 bcrypt 哈希。**对外部署前请修改或删除这两个账号。**

## 项目结构

```
src/main/java/com/springblog
├── config/      安全、MVC、缓存、MyBatis 扫描、@ConfigurationProperties
├── dao/         MyBatis Mapper 接口（SQL 在 resources/mapper/*.xml）
├── domain/      实体
├── dto/         表单与 JSON 响应对象
├── exception/   业务异常（404 / 403 / 401 / 409）
├── service/     业务规则：归属校验、事务、级联删除
├── support/     Markdown 渲染、视图格式化、当前登录人
└── web/         Controller（client 前台 / user 写作 / admin 后台）+ 全局异常处理
db/              schema.sql、data.sql
```

## 设计说明

### 1. 存储型 XSS：渲染管线只有一条

文章正文由用户输入，直接 `th:utext` 就是存储型 XSS。现在的处理是两步：

1. CommonMark 把 Markdown 转成 HTML；
2. OWASP `HtmlPolicyBuilder` 用白名单过滤：只留排版标签，`a` 只留 `href`，`img` 只留 `src/alt/title`，`class` 只允许 `language-*`（给 highlight.js 用），并给外链加 `rel="nofollow"`。

关键点：**CommonMark 原样透传裸 HTML**，所以第 2 步不能省。代码在 `support/MarkdownRenderer.java`，全文只有它的 `toSafeHtml()` 结果允许进 `th:utext`；其余场景（列表摘要、评论预览）一律走 `toPlainText()` + `th:text`。

### 2. CSRF

`SecurityConfig` 没有关 CSRF。两类请求各自带 token：

- 普通表单：用 `th:action`，Thymeleaf 会自动插入隐藏字段 `_csrf`；
- jQuery 的 ajax：页面头部渲染 `<meta name="_csrf">`，`static/js/csrf.js` 通过 `$.ajaxSetup` 给所有非 GET 请求加上 `X-CSRF-TOKEN` 头。

### 3. 越权与参数篡改

- 所有写操作都接收一个 `support/Actor`（用户名 + 是否管理员），归属判断 `actor.owns(author)` 放在 service 里，而不是散落在 Controller；这样规则可以直接单测，不需要 Web 上下文。
- 更新文章时，`author` 和 `created` 强制取数据库里的旧值，浏览器提交的同名字段会被覆盖——防的是"改别人的文章 / 改自己的发布日期"。
- 注册只接受 `RegisterForm` 的字段，`id`、`valid`、权限都无法从表单注入；新账号在事务里绑定 `ROLE_USER`。

### 4. 事务与级联

删除文章时，评论、点赞、收藏、统计在同一个事务里一起删；删除用户时通过文章 service 删掉其全部文章，再删资料和权限关联。否则外键虽没建，但唯一索引会被残留行占住。

### 5. 异常处理

`web/GlobalExceptionHandler` 统一把 `ResourceNotFoundException` / `NotOwnerException` / `UnauthenticatedException` 映射成 404 / 403 / 401，并按请求类型返回 HTML 错误页或 JSON。未预期异常的堆栈只进日志，浏览器只看到"系统错误，请稍后重试。"——不要把 `e.getMessage()` 直接回显。

### 6. 缓存

首页侧栏的"热门文章"用 `@Cacheable("mostViewedArticles")` + Caffeine（5 分钟过期）。发布、更新、删除文章时会 `@CacheEvict`。把 `CACHE_TYPE` 改成 `redis` 即可切到 Redis，代码不变。

### 7. 配置外部化

站点标题、根地址、摘要长度、缩略图兜底列表都在 `application.yml` 的 `blog.*` 下，绑定到 `config/BlogProperties`；数据库密码只从环境变量读，仓库里没有明文凭据。

## 手动验证清单

启动后可以按下面顺序演示（不需要写代码）：

| # | 操作 | 预期 |
| --- | --- | --- |
| 1 | 未登录时点"点赞" | 跳转登录页 |
| 2 | 用 curl 直接 POST `/article/like/1`（不带 token） | 403 |
| 3 | 在文章里写 `<script>alert(1)</script>` 并保存 | 页面不执行脚本，标签被剥离 |
| 4 | 用 `demo` 登录，访问 `/admin` | 403 |
| 5 | 用 `demo` 打开 `/user/article/edit/1`（admin 的文章） | 403 |
| 6 | 删除一篇文章后查库 | `t_comment` / `t_statistic` 中对应行一并消失 |
| 7 | 重复注册同一用户名 | 返回"用户名已存在"，不是 500 |

## 测试

```bash
mvn test    # 33 个用例，不需要 MySQL
```

| 测试类 | 覆盖内容 |
| --- | --- |
| `MarkdownRendererTest` | Markdown 正常渲染、`<script>`/`onclick`/`javascript:` 被剥离、摘要为纯文本 |
| `ArticleServiceImplTest` | 发布时作者取自登录人、更新时保留原作者与创建时间、非作者返回 403、删除级联、阅读计数 |
| `UserServiceImplTest` | 注册授予 `ROLE_USER`、并发唯一索引冲突转 409、空密码不重复加密 |
| `ArticleSecurityTest` | MockMvc 切片：无 CSRF token 的 POST 被 403 拒绝、匿名请求跳登录、校验失败不进 service |

## 已知限制

- 没有引入 Flyway / Liquibase，数据库结构变更靠手工执行 SQL。
- 没有对象存储和图片上传，缩略图用内置图片按 id 兜底。
- 没有限流和验证码，公开部署前应自行加上。
- 本项目按 MySQL 8 开发，其它数据库未适配。

## License

[MIT](LICENSE)
