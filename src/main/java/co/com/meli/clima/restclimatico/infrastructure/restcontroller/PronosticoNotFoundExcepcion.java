package co.com.meli.clima.restclimatico.infrastructure.restcontroller;

public class PronosticoNotFoundExcepcion extends RuntimeException{
    PronosticoNotFoundExcepcion(Integer id) {
        super("No se encontró pronostico para el día " + id);
    }
}
