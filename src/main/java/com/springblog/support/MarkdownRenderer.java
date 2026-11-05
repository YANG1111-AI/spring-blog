package com.springblog.support;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Renders user supplied Markdown into HTML that is safe to embed with th:utext.
 *
 * Two independent steps are required: CommonMark turns Markdown into HTML, and the OWASP
 * sanitizer strips anything executable, because CommonMark forwards raw HTML blocks untouched
 * and user content may contain them.
 */
@Component
public class MarkdownRenderer {

    private static final Pattern LANGUAGE_CLASS = Pattern.compile("(?i)language-[a-z0-9+#._-]{1,40}");

    /** highlight.js reads class="language-x"; nothing else is let through on class attributes. */
    private static final PolicyFactory SANITIZER = new HtmlPolicyBuilder()
            .allowStandardUrlProtocols()
            .allowElements(
                    "p", "br", "hr", "div", "span", "strong", "b", "em", "i", "del", "s",
                    "code", "kbd", "samp", "pre", "blockquote",
                    "ul", "ol", "li",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "a", "img",
                    "table", "thead", "tbody", "tfoot", "tr", "th", "td", "caption",
                    "figure", "figcaption")
            .allowAttributes("href").onElements("a")
            .allowAttributes("src", "alt", "title").onElements("img")
            .allowAttributes("class").matching(LANGUAGE_CLASS).onElements("code", "pre")
            .requireRelNofollowOnLinks()
            .toFactory();

    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    public MarkdownRenderer() {
        List<Extension> extensions = List.of(TablesExtension.create());
        this.parser = Parser.builder().extensions(extensions).build();
        this.htmlRenderer = HtmlRenderer.builder()
                .extensions(extensions)
                .softbreak("<br />")
                .build();
    }

    /**
     * Markdown to sanitized HTML. Only the returned value may be rendered with th:utext.
     */
    public String toSafeHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return SANITIZER.sanitize(htmlRenderer.render(document));
    }

    /**
     * Markdown to plain text: Markdown syntax and any raw HTML are dropped, whitespace collapsed.
     * Intended for th:text, which escapes the result again.
     */
    public String toPlainText(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        parser.parse(markdown).accept(new AbstractVisitor() {
            @Override
            public void visit(Text node) {
                text.append(node.getLiteral());
            }

            @Override
            public void visit(Code node) {
                text.append(node.getLiteral());
            }
        });
        return text.toString().replaceAll("\\s+", " ").trim();
    }

    /**
     * Plain-text excerpt, cut on a word boundary where possible.
     */
    public String toExcerpt(String markdown, int maxLength) {
        String plain = toPlainText(markdown);
        if (maxLength <= 0 || plain.length() <= maxLength) {
            return plain;
        }
        int cut = plain.lastIndexOf(' ', maxLength);
        if (cut <= 0) {
            cut = maxLength;
        }
        return plain.substring(0, cut) + "...";
    }
}
