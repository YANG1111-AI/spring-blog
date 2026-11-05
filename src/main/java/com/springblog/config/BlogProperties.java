package com.springblog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Site-wide settings that used to be hard-coded in the view helpers.
 */
@ConfigurationProperties(prefix = "blog")
public class BlogProperties {

    private final Site site = new Site();
    private final Content content = new Content();
    private final Thumbnails thumbnails = new Thumbnails();

    public Site getSite() {
        return site;
    }

    public Content getContent() {
        return content;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public static class Site {
        /** Public base URL, used for canonical links. Must match the deployed host. */
        private String url = "http://localhost:8080";
        private String title = "Spring Blog";
        private String description = "A lightweight personal blog platform.";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Content {
        private int excerptLength = 150;
        private String defaultCategory = "Uncategorized";

        public int getExcerptLength() {
            return excerptLength;
        }

        public void setExcerptLength(int excerptLength) {
            this.excerptLength = excerptLength;
        }

        public String getDefaultCategory() {
            return defaultCategory;
        }

        public void setDefaultCategory(String defaultCategory) {
            this.defaultCategory = defaultCategory;
        }
    }

    public static class Thumbnails {
        /** Fallback used when an article has no thumbnail of its own. */
        private String placeholder = "/user/img/rand/1.png";
        /** Deterministically assigned by article id so the list layout stays stable. */
        private List<String> fallbacks = new ArrayList<>();

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public List<String> getFallbacks() {
            return fallbacks;
        }

        public void setFallbacks(List<String> fallbacks) {
            this.fallbacks = fallbacks;
        }
    }
}
