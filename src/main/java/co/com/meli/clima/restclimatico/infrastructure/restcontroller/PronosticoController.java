package co.com.meli.clima.restclimatico.infrastructure.restcontroller;

import co.com.meli.clima.restclimatico.application.services.CalcularPronostico;
import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.dto.CargaPronostico;
import co.com.meli.clima.restclimatico.infrastructure.rabbitmq.InMensaje;
import co.com.meli.clima.restclimatico.infrastructure.rabbitmq.PronosticoJobConsumer;
import co.com.meli.clima.restclimatico.infrastructure.rabbitmq.PronosticoProducer;
import co.com.meli.clima.restclimatico.infrastructure.repository.PronosticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class PronosticoController {
    private Logger log = LoggerFactory.getLogger(PronosticoController.class);

    @Autowired
    PronosticoRepository pronosticoRepository;

    @Autowired
    InMensaje inMensaje;

    @Autowired
    PronosticoProducer pronosticoProducer;

    @Value("${pronostico.amqp.queue}")
    private String destino;

    @GetMapping("/clima")
    public Pronostico pronostico(@RequestParam(value="dia", defaultValue = "1") String dia){
        return pronosticoRepository.findById(Integer.valueOf(dia));
    }

    @PostMapping("/clima")
    public String cargarPronostico(@RequestBody CargaPronostico cargaPronostico){
        return inMensaje.enviarMensaje( destino,cargaPronostico.getIdPlaneta() + ":" + cargaPronostico.getAnios());
    }
}
