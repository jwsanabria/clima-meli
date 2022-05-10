package co.com.meli.clima.restclimatico.application.dto;

import lombok.Data;

@Data
public class Coordenada {

    private double x;
    private double y;

    public Coordenada(double x, double y) {
        this.x = x;
        this.y = y;
    }
}