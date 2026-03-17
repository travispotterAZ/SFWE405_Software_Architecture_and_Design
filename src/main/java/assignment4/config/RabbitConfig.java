package assignment4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;



@Configuration
public class RabbitConfig {

    // Queue for normal Person messages
    @Bean
    public Queue personQueue() {
        return new Queue("assignment4.person");
    }

    // Queue for validation error messages
    @Bean
    public Queue personErrorQueue() {
        return new Queue("assignment4.person.errors");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();           //Object for converting Java objects to JSON (both directions)
        mapper.registerModule(new JavaTimeModule());        //needed to support LocalDate variable for Birthdate
        return new Jackson2JsonMessageConverter(mapper);    //Tells RabbitMQ to use this converter for messages
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {  //connection Factory manages connection to RabbitMQ, MessageConverter is the unique one we just defined above
        RabbitTemplate template = new RabbitTemplate(connectionFactory);    //RabbitTemplate using connection factory to connect to RabbitMQ
        template.setMessageConverter(messageConverter);                     //Configures template to our JSON message converter
        return template;
    }

}
