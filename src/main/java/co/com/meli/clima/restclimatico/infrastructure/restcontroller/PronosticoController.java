package co.com.meli.clima.restclimatico.infrastructure.restcontroller;

import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.repository.PronosticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PronosticoController {

    @Autowired
    PronosticoRepository pronosticoRepository;

    @GetMapping("/clima")
    public Pronostico pronostico(@RequestParam(value="dia", defaultValue = "1") String dia){
        return pronosticoRepository.findById(Integer.valueOf(dia));
    }
}
