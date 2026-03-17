package assignment4.messaging;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import assignment4.data.PersonRepository;
import assignment4.model.Person;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;


@Component 
public class PersonListener {
    
    @Autowired
    private PersonRepository personRepo;

    private Validator validator;    //checks validation requirements for Person objects anntoated in Person.java
    private RabbitTemplate rabbit;  //used for sending messages (error ones in this case)

    public PersonListener(PersonRepository personRepo, Validator validator, RabbitTemplate rabbit){ //contructor
        this.personRepo = personRepo;
        this.validator = validator;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = "assignment4.person")  //assign queue to listen for messages
    public void receivePerson(Person person){
        Set<ConstraintViolation<Person>> violations = validator.validate(person);   //record any violations

        if(!violations.isEmpty()){ //check for violations
            System.out.println("Validations errors for received Person: " + person);
            for(ConstraintViolation<Person> v : violations) {
                System.out.println(" - " + v.getPropertyPath() + ": " + v.getMessage());
            }
            
            rabbit.convertAndSend("assignment4.person.errors", violations.toString());  //send all violaiton messages to error queue
            return; //no save of person if invalid
        }

        personRepo.save(person);    //if no violations save person to database
        System.out.println("Saved Person: " + person);
    }
    
}
