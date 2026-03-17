package assignment4.service;

import assignment4.model.Team;
import assignment4.data.TeamRepository;
import assignment4.model.Person;
import assignment4.data.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired; //SpringWeb 
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    
    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private PersonRepository personRepo;

    public List<Team> findAll(){
        return teamRepo.findAll();
    };

    public Team getTeamById(Long id) {
        return teamRepo.findById(id).orElse(null); //Fetches a Team by its ID, returns null if not found
    }

    public Team addTeam(Team team){
        List<Person> fullMembers = new ArrayList<>();
        for(Person p : team.getMembers()){
            Person checkPerson = personRepo.findById(p.getId()).orElse(null);
            if(checkPerson != null){
                fullMembers.add(checkPerson);
            }
        }
        team.getMembers().clear();  // remove stubs (null filled members) ; this allows for return of actual member data to POSTMAN view
        team.getMembers().addAll(fullMembers);

        return teamRepo.save(team);
    }


    public Team editTeam(Team updates){
        Team ogTeam = teamRepo.findById(updates.getId()).orElseThrow(
            () -> new RuntimeException("Team not found")
        );

        if(ogTeam.isEditable() == false){
            System.out.println("Team is NOT Editable!");
            return null; 
        }

        if (updates.getTeam_Name() != null) { ogTeam.setTeam_Name(updates.getTeam_Name()); }
        if (updates.getRank() != 0) { ogTeam.setRank(updates.getRank()); }
        if (updates.getState() != null) { ogTeam.setState(updates.getState()); }
        if (updates.getContest() != null) { ogTeam.setContest(updates.getContest()); }
        
        if (updates.getMembers() != null && !updates.getMembers().isEmpty()) {
            ogTeam.getMembers().clear();
            ogTeam.getMembers().addAll(updates.getMembers());
        }

        return teamRepo.save(ogTeam);
    }

    public Team setEditable(Team t){
        Team ogTeam = teamRepo.findById(t.getId()).orElseThrow(
            () -> new RuntimeException("Team not found")
        ); 

        ogTeam.setEditable(true);

        return teamRepo.save(ogTeam);
    }

    public Team setReadOnly(Team t){
        Team ogTeam = teamRepo.findById(t.getId()).orElseThrow(
            () -> new RuntimeException("Team not found")
        ); 

        ogTeam.setEditable(false);

        return teamRepo.save(ogTeam);
    }
}
