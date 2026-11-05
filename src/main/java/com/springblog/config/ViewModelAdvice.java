package com.springblog.config;

import com.springblog.support.ViewFormatter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Publishes the view helpers to every template, replacing the per-controller
 * model.addAttribute("commons", commons) calls the original code repeated in 20+ handlers.
 */
@ControllerAdvice
public class ViewModelAdvice {

    private final ViewFormatter viewFormatter;

    public ViewModelAdvice(ViewFormatter viewFormatter) {
        this.viewFormatter = viewFormatter;
    }

    @ModelAttribute("commons")
    public ViewFormatter commons() {
        return viewFormatter;
    }
}
