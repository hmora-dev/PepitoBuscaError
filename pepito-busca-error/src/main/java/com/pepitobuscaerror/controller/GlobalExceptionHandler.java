package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.service.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFound(ResourceNotFoundException exception, Model model) {
		model.addAttribute("title", "Page not found");
		model.addAttribute("message", exception.getMessage());
		return "error/404";
	}

	@ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleMissingRoute(Exception exception, Model model) {
		model.addAttribute("title", "Page not found");
		model.addAttribute("message", "The requested page does not exist.");
		return "error/404";
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleInvalidRouteParameter(MethodArgumentTypeMismatchException exception, Model model) {
		model.addAttribute("title", "Page not found");
		model.addAttribute("message", "The requested page does not exist.");
		return "error/404";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleUnexpectedError(Exception exception, Model model) {
		model.addAttribute("title", "Something went wrong");
		model.addAttribute("message", "The application could not complete the requested action. Please return to the dashboard and try again.");
		return "error/500";
	}
}
