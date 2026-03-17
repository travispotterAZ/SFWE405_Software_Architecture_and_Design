package assignment4.clients;

import assignment4.Assignment4Application;
import assignment4.messaging.RabbitPersonMessagingService;  //rabbit messenger sending
import assignment4.model.Person;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner; //Input scanner
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RabbitClient {
    
    public static void main(String[] args){
        //Spring context for rabbit client
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Assignment4Application.class).web(WebApplicationType.NONE).run(args);

        //Gets the RabbitPersonMessagingService bean from the Spring context
        RabbitPersonMessagingService messagingService = context.getBean(RabbitPersonMessagingService.class); //Get bean for messaging service

        Scanner scanner = new Scanner(System.in); //terminal scanner for inputs
        
        System.out.println();
        System.out.println("-------------------------------");
        System.out.println("Enter 'exit' in name input to quit");
        System.out.println("Birthday inputs are of form: yyyy-MM-DD");
        System.out.println("Role types: Student, Coach, Manager");
        System.out.println();

        while(true){
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            if(name.equals("exit")){break;}
            
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter birthdate: ");
            String birthday = scanner.nextLine();
            LocalDate birthdate;
            try{
                birthdate = LocalDate.parse(birthday);
            } catch (DateTimeParseException e){
                System.out.println("Invalid birthdate input.");
                System.out.println();
                continue;
            }
        
            System.out.print("Enter university: ");
            String university = scanner.nextLine();

            System.out.print("Enter person Role: ");
            String role = scanner.nextLine();
            Person.Role checkRole;
            try{
                checkRole = Person.Role.valueOf(role);
            } catch (IllegalArgumentException e){
                System.out.println("Invalid role input.");
                System.out.println();
                continue;
            }
            
            Person person = new Person();
            person.setName(name);
            person.setEmail(email);
            person.setBirthdate(birthdate);
            person.setUniversity(university);
            person.setRole(checkRole);

            messagingService.sendPerson(person);    //Sends person object to RabbitMQ using the messaging service

            System.out.println("Person sent to RabbitMQ!");
            System.out.println("-------------------------------");



        }

    context.close();
    scanner.close();
    System.out.println("Rabbit Client exited");

    }
}
