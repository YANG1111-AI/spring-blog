package com.springblog.support;

import com.springblog.config.BlogProperties;
import com.springblog.domain.Article;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Formatting helpers exposed to every Thymeleaf template as the {@code commons} model attribute.
 *
 * Content rendering is delegated to {@link MarkdownRenderer}; this class only decides which
 * representation a template needs: sanitized HTML (th:utext) or plain text (th:text).
 */
@Component
public class ViewFormatter {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MarkdownRenderer markdownRenderer;
    private final BlogProperties properties;

    public ViewFormatter(MarkdownRenderer markdownRenderer, BlogProperties properties) {
        this.markdownRenderer = markdownRenderer;
        this.properties = properties;
    }

    public String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_TIME.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /** Full article body as sanitized HTML. The only value safe for th:utext. */
    public String renderContent(String content) {
        return markdownRenderer.toSafeHtml(content);
    }

    /** Plain-text excerpt for list pages and comment previews; render with th:text. */
    public String excerpt(String content) {
        return excerpt(content, properties.getContent().getExcerptLength());
    }

    public String excerpt(String content, int maxLength) {
        return markdownRenderer.toExcerpt(content, maxLength);
    }

    public String excerpt(Article article) {
        return article == null ? "" : excerpt(article.getContent());
    }

    public String permalink(Integer articleId) {
        return articleId == null ? "/" : "/article/" + articleId;
    }

    /**
     * Article thumbnail, falling back to a deterministic bundled image so list layouts stay stable.
     */
    public String thumbnail(Article article) {
        if (article == null) {
            return properties.getThumbnails().getPlaceholder();
        }
        String thumbnail = article.getThumbnail();
        if (thumbnail != null && !thumbnail.isBlank()) {
            return thumbnail;
        }
        if (article.getId() == null) {
            return properties.getThumbnails().getPlaceholder();
        }
        return isBlankList(properties.getThumbnails().getFallbacks())
                ? properties.getThumbnails().getPlaceholder()
                : properties.getThumbnails().getFallbacks()
                        .get(Math.floorMod(article.getId(), properties.getThumbnails().getFallbacks().size()));
    }

    public String siteUrl() {
        return properties.getSite().getUrl();
    }

    public String siteUrl(String path) {
        String base = siteUrl();
        if (path == null || path.isEmpty()) {
            return base;
        }
        return base + (path.startsWith("/") ? path : "/" + path);
    }

    public String siteTitle() {
        return properties.getSite().getTitle();
    }

    public String siteDescription() {
        return properties.getSite().getDescription();
    }

    private static boolean isBlankList(java.util.List<String> values) {
        return values == null || values.isEmpty();
    }
}
