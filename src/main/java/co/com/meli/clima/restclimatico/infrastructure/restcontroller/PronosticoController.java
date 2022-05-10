package co.com.meli.clima.restclimatico.infrastructure.restcontroller;

import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.dto.CargaPronostico;
import co.com.meli.clima.restclimatico.infrastructure.rabbitmq.InMensaje;
import co.com.meli.clima.restclimatico.infrastructure.repository.PronosticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PronosticoController {

    @Autowired
    PronosticoRepository pronosticoRepository;

    @Autowired
    InMensaje inMensaje;

    @GetMapping("/clima")
    public Pronostico pronostico(@RequestParam(value="dia", defaultValue = "1") String dia){
        return pronosticoRepository.findById(Integer.valueOf(dia));
    }

    @PostMapping("/clima")
    public String cargarPronostico(@RequestBody CargaPronostico cargaPronostico){
        return inMensaje.enviarMensaje(cargaPronostico.getIdPlaneta() + ":" + cargaPronostico.getAnios());
    }
}
