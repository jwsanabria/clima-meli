package co.com.meli.clima.restclimatico.infrastructure.rabbitmq;

import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.repository.PronosticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PronosticoJobConsumer {
    private Logger log = LoggerFactory.getLogger(PronosticoJobConsumer.class);
    private PronosticoRepository repository;

    public PronosticoJobConsumer(PronosticoRepository repository){
        this.repository = repository;
    }

    @RabbitListener(queues = "${pronostico.amqp.queue}")
    public void procesarPronostico(String pronostico){
        log.info("Consumer> " + pronostico);
        log.info("ToDo created> " + this.repository.findById(1));
    }
}
