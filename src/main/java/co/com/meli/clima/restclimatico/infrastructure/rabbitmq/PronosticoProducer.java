package co.com.meli.clima.restclimatico.infrastructure.rabbitmq;

import co.com.meli.clima.restclimatico.infrastructure.dto.CargaPronostico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PronosticoProducer {
    private static final Logger log = LoggerFactory.getLogger(PronosticoProducer.class);
    private RabbitTemplate template;

    public PronosticoProducer(RabbitTemplate template){
        this.template = template;
    }
    public void sendTo(String queue, CargaPronostico cargaPronostico){
        this.template.convertAndSend(queue,cargaPronostico);
        log.info(" [x] Message Sent" + cargaPronostico.getIdPlaneta()+":"+cargaPronostico.getAnios());
    }
}
