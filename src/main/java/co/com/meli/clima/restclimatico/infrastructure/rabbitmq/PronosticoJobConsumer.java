package co.com.meli.clima.restclimatico.infrastructure.rabbitmq;

import co.com.meli.clima.restclimatico.application.services.CalcularPronostico;
import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.repository.PronosticoRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PronosticoJobConsumer {
    private Logger log = LoggerFactory.getLogger(PronosticoJobConsumer.class);
    private PronosticoRepository repository;

    private CalcularPronostico calcularPronostico;

    public PronosticoJobConsumer(PronosticoRepository repository, CalcularPronostico calcularPronostico){
        this.repository = repository;
        this.calcularPronostico = calcularPronostico;
    }

    @RabbitListener(queues = "${pronostico.amqp.queue}")
    public void procesarPronostico(@NotNull String message){
        log.info("Consumer> " + message);
        if(message.contains(":")) {
            String[] array = message.split(":");
            log.info(" [x] Received '" + message + "'");
            try {
                this.calcularPronostico.realizarPronostico(Integer.getInteger(array[0]), Integer.getInteger(array[1]));
            }catch (Exception e){
                log.warn("No se puede ejecutar el pronostico");
            }
        }

    }
}
