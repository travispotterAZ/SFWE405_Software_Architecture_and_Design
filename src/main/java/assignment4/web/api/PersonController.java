package assignment4.web.api;

import assignment4.model.Person;
import assignment4.service.PersonService;

import org.springframework.web.bind.annotation.RestController; //Spring Web
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
//import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private PersonService personService;
    
    @GetMapping("/all")
    public List<Person> GetAllPeople(){
        return personService.findAll();
    }

    @PostMapping("/addPerson") //general add person (Specific to assignment 4)
    public Person addPerson(@Valid @RequestBody Person person){
        return personService.addPerson(person);
    }

    @PostMapping("/addMember")
    public Person createMember(@RequestBody Person person){
        return personService.addMember(person);
    }

    @PostMapping("/addCoach")
    public Person createCoach(@RequestBody Person person){
        return personService.addCoach(person);
    }

    @PostMapping("/addManager")
    public Person createManager(@RequestBody Person person){
        return personService.addManager(person);
    }
}
