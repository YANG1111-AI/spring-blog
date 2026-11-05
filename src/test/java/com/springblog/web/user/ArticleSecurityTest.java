package com.springblog.web.user;

import com.springblog.service.IArticleService;
import com.springblog.config.SecurityConfig;
import com.springblog.support.CurrentUser;
import com.springblog.support.ViewFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice test for the two protections that are easy to lose during a refactor: the CSRF filter and
 * the authorization rules on /user/**. No database is involved.
 */
@WebMvcTest(controllers = ArticleController.class)
@Import({SecurityConfig.class, CurrentUser.class})
class ArticleSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IArticleService articleService;

    /** Only needed so ViewModelAdvice can be instantiated. */
    @MockBean
    private ViewFormatter viewFormatter;

    @Test
    void postWithoutCsrfTokenIsRejectedBeforeTheControllerRuns() throws Exception {
        mvc.perform(post("/user/article/publish")
                        .with(user("alice").roles("USER"))
                        .param("title", "t")
                        .param("content", "c"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(articleService);
    }

    @Test
    void postWithCsrfTokenBySignedInUserIsAccepted() throws Exception {
        mvc.perform(post("/user/article/publish")
                        .with(user("alice").roles("USER"))
                        .with(csrf())
                        .param("title", "t")
                        .param("content", "c"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/article/my?published=true"));

        verify(articleService).publish(any(), any());
    }

    @Test
    void anonymousPostIsSentToTheLoginPage() throws Exception {
        mvc.perform(post("/user/article/publish")
                        .with(csrf())
                        .param("title", "t")
                        .param("content", "c"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoInteractions(articleService);
    }

    @Test
    void invalidArticleIsNotSentToTheService() throws Exception {
        mvc.perform(post("/user/article/publish")
                        .with(user("alice").roles("USER"))
                        .with(csrf())
                        .param("title", "   ")
                        .param("content", "c"))
                .andExpect(status().isOk());

        verifyNoInteractions(articleService);
    }
}
