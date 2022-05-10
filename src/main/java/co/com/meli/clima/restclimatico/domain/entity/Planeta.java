package co.com.meli.clima.restclimatico.domain.entity;
import lombok.Data;

@Data
public class Planeta {
    private Integer id;
    private String nombre;
    private Integer radio;
    private Integer velocidad;
    private Boolean horario;

}