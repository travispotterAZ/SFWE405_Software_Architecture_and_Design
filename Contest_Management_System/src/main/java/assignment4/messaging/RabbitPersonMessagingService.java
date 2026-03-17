package assignment4.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.stereotype.Service;

import assignment4.model.Person;

@Service
public class RabbitPersonMessagingService {
    private RabbitTemplate rabbit;  //sending messages

    public RabbitPersonMessagingService(RabbitTemplate rabbit){ //constructor 
        this.rabbit = rabbit;
    }

    public void sendPerson(Person p) {
        MessageConverter converter = rabbit.getMessageConverter();  //retrieves Person to rabbit message converter
        MessageProperties props = new MessageProperties();          //creates message properties, allows for customization of the message (future uses)
        Message message = converter.toMessage(p, props);            //uses converter and creates acceptable RabbitMQ message
        rabbit.send("assignment4.person", message);                 //sends message to queue specified
    }
}
