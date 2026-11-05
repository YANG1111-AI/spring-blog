package com.springblog.web;

import com.springblog.exception.BlogException;
import com.springblog.exception.NotOwnerException;
import com.springblog.exception.ResourceNotFoundException;
import com.springblog.exception.UnauthenticatedException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Replaces the per-handler try/catch blocks: unexpected failures are logged once with the stack
 * trace and reported to the browser as a short message, so internal exception text never reaches
 * a page or a JSON payload.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView notFound(HttpServletRequest request, HandlerMethod handler, Exception ex) {
        return respond(request, handler, HttpStatus.NOT_FOUND, message(ex, "内容不存在。"));
    }

    @ExceptionHandler(NotOwnerException.class)
    public ModelAndView notOwner(HttpServletRequest request, HandlerMethod handler, Exception ex) {
        log.warn("Ownership check failed for {}: {}", request.getRequestURI(), ex.getMessage());
        return respond(request, handler, HttpStatus.FORBIDDEN, message(ex, "您没有权限操作该内容。"));
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ModelAndView unauthenticated(HttpServletRequest request, HandlerMethod handler, Exception ex) {
        return respond(request, handler, HttpStatus.UNAUTHORIZED, "请先登录。");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ModelAndView invalid(HttpServletRequest request, HandlerMethod handler, BindException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(GlobalExceptionHandler::describe)
                .orElse("提交的内容不符合要求。");
        return respond(request, handler, HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * Anything not already classified: log it, show a generic sentence.
     */
    @ExceptionHandler(BlogException.class)
    public ModelAndView blogFailure(HttpServletRequest request, HandlerMethod handler, BlogException ex) {
        log.warn("Handled failure on {}: {}", request.getRequestURI(), ex.getMessage());
        return respond(request, handler, HttpStatus.INTERNAL_SERVER_ERROR, message(ex, "操作未完成，请稍后重试。"));
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView unexpected(HttpServletRequest request, HandlerMethod handler, Exception ex) {
        log.error("Unhandled exception on {}", request.getRequestURI(), ex);
        return respond(request, handler, HttpStatus.INTERNAL_SERVER_ERROR, "系统错误，请稍后重试。");
    }

    private ModelAndView respond(HttpServletRequest request, HandlerMethod handler,
                                 HttpStatus status, String message) {
        ModelAndView view = wantsJson(request, handler)
                ? jsonView(status, message)
                : htmlView(status, message);
        view.setStatus(status);
        return view;
    }

    private ModelAndView jsonView(HttpStatus status, String message) {
        ModelAndView view = new ModelAndView(new MappingJackson2JsonView());
        view.addObject("success", false);
        view.addObject("message", message);
        view.addObject("data", null);
        if (status == HttpStatus.UNAUTHORIZED) {
            view.addObject("loginRequired", true);
        }
        return view;
    }

    private ModelAndView htmlView(HttpStatus status, String message) {
        String template = switch (status) {
            case NOT_FOUND -> "comm/error_404";
            case FORBIDDEN, UNAUTHORIZED -> "comm/error_403";
            default -> "client/error";
        };
        ModelAndView view = new ModelAndView(template);
        view.addObject("error", message);
        view.addObject("status", status.value());
        return view;
    }

    private static boolean wantsJson(HttpServletRequest request, HandlerMethod handler) {
        if (handler != null
                && (handler.hasMethodAnnotation(ResponseBody.class)
                || AnnotatedElementUtils.hasAnnotation(handler.getBeanType(), ResponseBody.class))) {
            return true;
        }
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private static String describe(FieldError error) {
        return error.getDefaultMessage() != null ? error.getDefaultMessage() : error.getField() + " 不合法。";
    }

    private static String message(Exception ex, String fallback) {
        return ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : fallback;
    }
}
