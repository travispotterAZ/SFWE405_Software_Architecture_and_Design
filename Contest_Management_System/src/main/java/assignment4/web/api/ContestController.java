package assignment4.web.api;

import assignment4.service.ContestService;
import assignment4.model.Team;
import assignment4.model.Contest;

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
@RequestMapping("/contests")       //base URL path for endpoints of ContestController
public class ContestController {
    
    @Autowired
    private ContestService contestService;

    @GetMapping("/all")
    public List<Contest> findAll(){
        return contestService.findAll();
    }

    @GetMapping("{id}")
    public Contest getById(@PathVariable Long id){
        return contestService.getById(id);
    }

    @GetMapping("/{contestName}/teams")
    public List<Team> findTeamsofContest(@PathVariable String contestName) {    //Endpoint to get Teams in a Contest
        return contestService.getTeamsByContestName(contestName);
    }

    @GetMapping("/{contestName}/occupancy")
    public String getContestOccupancyVsCapacity(@PathVariable String contestName) {    //Endpoint to get Occupancy vs. Capacityt for a Contest
        return contestService.getContestOccVSCap(contestName);
    }

    @PostMapping("/addContest")
    public Contest addContest(@RequestBody Contest contest){
        return contestService.addContest(contest);
    }

    @PostMapping("/teamReg")
    public Team teamReg(@RequestParam Long contest_id, @RequestBody Team team){
        return contestService.teamReg(contest_id, team);
    }

    @PostMapping("/editContest")
    public Contest editContest(@RequestBody Contest updates){
        return contestService.editContest(updates);
    }
    
    @PostMapping("/setEditable")
    public Contest setEditable(@RequestBody Contest c){
        return contestService.setEditable(c);
    }

    @PostMapping("/setReadOnly")
    public Contest setReadOnly(@RequestBody Contest c){
        return contestService.setReadOnly(c);
    }

    @PostMapping("/promoteTeam")
    public Team promoteTeam(@RequestParam Long superContest_id, @RequestParam Long team_id){
        return contestService.promoteTeam(superContest_id, team_id);
    }
}
