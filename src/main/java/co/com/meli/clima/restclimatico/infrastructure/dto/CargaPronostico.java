package co.com.meli.clima.restclimatico.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CargaPronostico {
    private String idPlaneta;
    private String anios;

    @Override
    public String toString() {
        return "{" +
                "idPlaneta='" + idPlaneta + '\'' +
                ", anios='" + anios + '\'' +
                '}';
    }
}
