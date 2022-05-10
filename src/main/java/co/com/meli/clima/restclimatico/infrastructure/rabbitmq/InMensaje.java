package co.com.meli.clima.restclimatico.infrastructure.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class InMensaje {
    final static Logger logger = LoggerFactory.getLogger(InMensaje.class);

    public String enviarMensaje(String msg){
        String resultado = "";
        try {
            ConnectionFactory factory = new ConnectionFactory();
            String uri = System.getenv("CLOUDAMQP_URL");
            if (uri == null) uri = "amqp://guest:guest@localhost";
            factory.setUri(uri);

            //Recommended settings
            factory.setConnectionTimeout(30000);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String queueName = "work-queue-1";
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("x-ha-policy", "all");
            channel.queueDeclare(queueName, true, false, false, params);


            byte[] body = msg.getBytes("UTF-8");
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, body);
            logger.info("Message Sent: " + msg);
            connection.close();

            resultado =  "Message Sent: " + msg;

        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultado = "No se logró escribir en la cola";
        }
        return resultado;
    }
}
