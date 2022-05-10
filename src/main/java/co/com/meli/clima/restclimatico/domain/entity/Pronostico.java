package co.com.meli.clima.restclimatico.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pronostico {
    private int dia;
    private String clima;

}
