package co.com.meli.clima.restclimatico.application.dto;

import co.com.meli.clima.restclimatico.domain.entity.Planeta;
import lombok.Data;

import java.util.Map;

@Data
public class InfoPlanetaria {
    private Planeta planeta;
    private Map<Integer, CoordenadasDiarias> movimientosDiarios;

}
