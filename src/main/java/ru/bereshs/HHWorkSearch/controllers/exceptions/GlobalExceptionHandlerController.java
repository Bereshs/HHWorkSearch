package ru.bereshs.HHWorkSearch.controllers.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController extends Exception{

    private final Logger logger = Logger.getLogger(GlobalExceptionHandlerController.class.getName());
    public String NullPointerExceptionHandler (NullPointerException exception, Model model) {
        model.addAttribute("errorMessage", exception.fillInStackTrace());
        logger.info(exception.getLocalizedMessage());
        return "/error";
    }


}
