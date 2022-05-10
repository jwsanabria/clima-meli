package co.com.meli.clima.restclimatico.infrastructure.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class PronosticoNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(PronosticoNotFoundExcepcion.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pronosticoNotFoundHandler(PronosticoNotFoundExcepcion ex) {
        return ex.getMessage();
    }
}
