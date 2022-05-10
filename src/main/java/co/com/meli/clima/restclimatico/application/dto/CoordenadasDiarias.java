package co.com.meli.clima.restclimatico.application.dto;
import lombok.Data;

@Data
public class CoordenadasDiarias {
    private int dia;
    private Coordenada coordenada;
    private int angulo;
}
