package com.springblog.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The renderer is the single place where user supplied Markdown becomes HTML, so these tests pin
 * down the behaviour that the rest of the app relies on: formatting survives, executable input
 * does not.
 */
class MarkdownRendererTest {

    private final MarkdownRenderer renderer = new MarkdownRenderer();

    @Test
    void rendersHeadingsBoldAndCode() {
        String html = renderer.toSafeHtml("# Title\n\n**bold** and `code`");

        assertThat(html).contains("<h1>Title</h1>")
                .contains("<strong>bold</strong>")
                .contains("<code>code</code>");
    }

    @Test
    void rendersGithubTables() {
        String html = renderer.toSafeHtml("""
                | a | b |
                |---|---|
                | 1 | 2 |
                """);

        assertThat(html).contains("<table>").contains("<th>a</th>").contains("<td>1</td>");
    }

    @Test
    void removesScriptElements() {
        String html = renderer.toSafeHtml("before\n\n<script>alert('xss')</script>\n\nafter");

        assertThat(html).doesNotContainIgnoringCase("<script")
                .doesNotContain("alert('xss')")
                .contains("before")
                .contains("after");
    }

    @Test
    void removesEventHandlersFromLinks() {
        String html = renderer.toSafeHtml("<a href=\"/article/1\" onclick=\"alert(1)\">link</a>");

        assertThat(html).doesNotContain("onclick").contains("href=\"/article/1\"");
    }

    @Test
    void removesJavascriptUrlsFromImages() {
        String html = renderer.toSafeHtml("![x](javascript:alert(1))");

        assertThat(html).doesNotContain("javascript:").doesNotContain("src=");
    }

    @Test
    void keepsLanguageClassForSyntaxHighlighting() {
        String html = renderer.toSafeHtml("```java\nint x = 1;\n```");

        assertThat(html).contains("class=\"language-java\"");
    }

    @Test
    void blankInputRendersNothing() {
        assertThat(renderer.toSafeHtml(null)).isEmpty();
        assertThat(renderer.toSafeHtml("   ")).isEmpty();
        assertThat(renderer.toPlainText(null)).isEmpty();
    }

    @Test
    void plainTextDropsMarkdownAndHtml() {
        String text = renderer.toPlainText("**bold** text\n\n<script>alert(1)</script>");

        assertThat(text).contains("bold").contains("text")
                .doesNotContain("**")
                .doesNotContain("<script>");
    }

    @Test
    void excerptIsPlainTextAndBounded() {
        String excerpt = renderer.toExcerpt("**one** two three four five", 8);

        assertThat(excerpt).doesNotContain("**").endsWith("...");
        assertThat(excerpt.length()).isLessThanOrEqualTo(11);
    }
}
