package assignment4.service;

import org.springframework.beans.factory.annotation.Autowired; //SpringWeb
import org.springframework.stereotype.Service;

import assignment4.data.PersonRepository;
import assignment4.model.Person;
import assignment4.data.TeamRepository;
import assignment4.model.Team;

import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepo;

    @Autowired
    private TeamRepository teamRepo;

    public List<Person> findAll(){
        return personRepo.findAll();
    }

    public Person addMember(Person person){
        return personRepo.save(person);
    }

    public Person addCoach(Person person){
        return personRepo.save(person);
    }

    public Person addManager(Person person){
        return personRepo.save(person);
    }

    public Person addPerson(Person person){
        return personRepo.save(person);
    }

    public Team  assignTeamToCoach(Long team_id, Long coach_id){
        Person coach = personRepo.findById(coach_id).orElse(null);
        Team team = teamRepo.findById(team_id).orElse(null);
        
        if(coach != null && team != null){
            coach.getCoached_teams().add(team);
            personRepo.save(coach);
        }

        return team;
    }
}
