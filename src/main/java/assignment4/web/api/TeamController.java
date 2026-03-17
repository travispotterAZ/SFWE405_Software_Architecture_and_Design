package assignment4.web.api;

import assignment4.model.Team;
import assignment4.service.TeamService;
import assignment4.service.PersonService;

import org.springframework.web.bind.annotation.RestController; //Spring Web
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@RestController
@RequestMapping("/teams")       //base URL path for endpoints of TeamsController
public class TeamController {
    
    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonService personService;

    @GetMapping("/all")
    public List<Team> findAll() {
        return teamService.findAll();
    }

    @GetMapping("/{id}")
    public Team getTeamById(@PathVariable Long id) {    //Endpoint to get a Team by its ID, e.g., GET /teams/1
        return teamService.getTeamById(id);
    }

    @PostMapping("/addTeam")
    public Team addTeam(@RequestBody Team team){
        return teamService.addTeam(team);
    }

    @PostMapping("/assignTeamToCoach")
    public Team assignCoach(@RequestParam Long team_id, @RequestParam Long coach_id){
        return personService.assignTeamToCoach(team_id, coach_id);
    }

    @PostMapping("/editTeam")
    public Team editTeam(@RequestBody Team updates){
        return teamService.editTeam(updates);
    }

    @PostMapping("/setEditable")
    public Team setEditable(@RequestBody Team t){
        return teamService.setEditable(t);
    }

    @PostMapping("/setReadOnly")
    public Team setReadOnly(@RequestBody Team t){
        return teamService.setReadOnly(t);
    }

}
