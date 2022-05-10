package co.com.meli.clima.restclimatico.infrastructure.rabbitmq;

import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import co.com.meli.clima.restclimatico.infrastructure.dto.CargaPronostico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class PronosticoSchedulerSender {
    @Autowired
    private PronosticoProducer producer;
    @Value("${pronostico.amqp.queue}")
    private String destino;
    @Scheduled(cron="@daily")
    public CommandLineRunner enviarMensaje(){

        return args -> {
            CargaPronostico carga = new CargaPronostico();
            carga.setAnios("10");
            carga.setIdPlaneta("2");
            producer.sendTo(destino,carga);
        };
    }
}
