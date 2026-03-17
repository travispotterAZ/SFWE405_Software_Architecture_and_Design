package assignment4.service;

import org.springframework.beans.factory.annotation.Autowired; //SpringWeb
import org.springframework.stereotype.Service;

import assignment4.data.ContestRepository;
import assignment4.data.TeamRepository;
import assignment4.data.PersonRepository;
import assignment4.model.Contest;
import assignment4.model.Person;
import assignment4.model.Team;
import assignment4.model.Team.State;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ContestService {
    
    @Autowired
    private ContestRepository contestRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private PersonRepository personRepo;

    public List<Contest> findAll(){
        return contestRepo.findAll();
    }

    public Contest getById(Long id){
        return contestRepo.findById(id).orElseThrow(
            () -> new RuntimeException("Contest not found")
        );
    }

    //SELECT All Teams in a Contest
    public List<Team> getTeamsByContestName(String contestName){
        Contest contest = contestRepo.findByName(contestName); //Fetches a Contest by name, if not foudn returns null

        if (contest == null) {
            return new ArrayList<>();
        }

        return teamRepo.findByContest(contest); //Returns a list of all Teams registered to contet
    }

    //Contest Occupancy vs. Capacity
    public String getContestOccVSCap(String name){
        Contest contest = contestRepo.findByName(name); //Fetches a Contest by name, if not foudn returns null
        if (contest == null) {
            return "Contest not found: " + name;
        }

        List<Team> teams = teamRepo.findByContest(contest);

        int occupancy = teams.size(); //number of teams currently registered
		int capacity = contest.getCapacity(); //maximum capacity of contest

        boolean full = occupancy >= capacity;

        return "Contest: " + contest.getName() +
           " | Capacity: " + capacity +
           " | Current Occupancy: " + occupancy +
           " | Full: " + full;
    }

    public Contest addContest(Contest contest){
        return contestRepo.save(contest);
    }

    public Team teamReg(Long contest_id, Team teamInput){
        Contest updatedContest = contestRepo.findById(contest_id)
                .orElseThrow(() -> new RuntimeException("Contest not found")
        );

        Team team = teamRepo.findById(teamInput.getId()).orElseThrow(
                                                        () -> new IllegalArgumentException("Team not found")
        );
        
        
        if(eligibilityCheck(contest_id, team)){
            team.setContest(updatedContest);
            team.setState(State.Accepted);
            return teamRepo.save(team);
        }
        team.setState(State.Cancelled);
        System.out.println("Not eligible");
        return teamRepo.save(team); //Not eligible
    }

    public boolean eligibilityCheck(Long contest_id, Team team){
        //Implement Rules for registration

        //One Coach
        List<Person> coaches = personRepo.findCoachesForTeam(team);
        if(coaches.size() != 1){ //Must Have 1 Coach
            System.out.println("Not eligible: Coach Violation");
            return false; //No coaches or too many
        }

        //Three Team Members
        if(team.getMembers().size() != 3){
            System.out.println("Not eligible: Member Violation" + "/n Num Members = " + team.getMembers().size() );
            return false; //Less than three members
        }


        //All Members Distinct
        Set<Long> uniqueIds = new HashSet<>();
        for (Person p : team.getMembers()) { uniqueIds.add(p.getId()); }
        for (Person c : coaches) { uniqueIds.add(c.getId()); }
        int expectedSize = team.getMembers().size() + coaches.size();
        if (uniqueIds.size() != expectedSize) {
                System.out.println("Not eligible: Distinction Violation");
                return false; //uniqueIds is less than number members total so one must be duplicated
        }

        //Contest Capacity
        Contest contest = contestRepo.findById(contest_id).orElseThrow(() -> new RuntimeException("Contest not found"));;
        List<Team> teamsRegistered = teamRepo.findByContest(contest); //Might need to use find by contest name

        int occupancy = teamsRegistered.size(); //number of teams currently registered
		int capacity = contest.getCapacity(); //maximum capacity of contest

        if(occupancy >= capacity){
            System.out.println("Not eligible: Occupancy Violation");
            return false; //Contest is full
        }

        //Members: 24yrs or under
        for(Person p : team.getMembers()){
            if(p.getAge() >= 24){
                System.out.println("Not eligible: Age Violation");
                return false; //A member is not younger than 24
            }
        }

        //Members not on another team in the contest
        for(Person p : team.getMembers()){
            for(Team t : teamsRegistered){
                for(Person p2 : t.getMembers()){
                    if(p.getId() == p2.getId()){
                        System.out.println("Not eligible: MemberToTeam Violation");
                        return false; //member on newTeam already exists on a different team in Contest
                    }
                }
            }
        }

        System.out.println("Eligible");
        return true; //Eligible (passed all tests)
    }

    public Contest editContest(Contest updates){
        Contest ogContest = contestRepo.findById(updates.getId()).orElseThrow(
             () -> new RuntimeException("Contest not found")
        );

        if(ogContest.isEditable() == false){
            System.out.println("Contest is NOT Editable!");
            return null;
        }

        // Update fields that may have changed
        if (updates.getName() != null) ogContest.setName(updates.getName());
        if (updates.getCapacity() != 0) ogContest.setCapacity(updates.getCapacity());
        if (updates.getDate() != null) ogContest.setDate(updates.getDate());
        ogContest.setRegistration_allowed(updates.isRegistration_allowed());
        if (updates.getRegistration_from() != null) ogContest.setRegistration_from(updates.getRegistration_from());
        if (updates.getRegistration_to() != null) ogContest.setRegistration_to(updates.getRegistration_to());
        if (updates.getPreliminary_round() != null) ogContest.setPreliminary_round(updates.getPreliminary_round());

    return contestRepo.save(ogContest); // persist changes
    }

    public Contest setEditable(Contest c){
        Contest ogContest = contestRepo.findById(c.getId()).orElseThrow(
            () -> new RuntimeException("Contest not found")
        ); 

        ogContest.setEditable(true);

        return contestRepo.save(ogContest);
    }

    public Contest setReadOnly(Contest c){
        Contest ogContest = contestRepo.findById(c.getId()).orElseThrow(
            () -> new RuntimeException("Contest not found")
        ); 

        ogContest.setEditable(false);

        return contestRepo.save(ogContest);
    }

    public Team promoteTeam(Long superContest_id, Long team_id){
        Contest superC = contestRepo.findById(superContest_id).orElseThrow(
            () -> new RuntimeException("Contest not found")
        );
        Team team = teamRepo.findById(team_id).orElseThrow(
            () -> new RuntimeException("Team not found")
        );
        
        if(eligibilityCheck(superContest_id, team) == false){ //Original Eligibility checks
            System.out.println("Team failed Eligibility Check 1.");
            return null;
        }

        if(team.getRank() > 5 || team.getRank() < 1 ){ //Rank Check
            System.out.println("Team failed Eligibility Check 2.");
            System.out.println("Team Rank not Satisfactory  --- Rank: " + team.getRank());
            return null;
        }
        
        Team teamClone = new Team();
        teamClone.setId(null);
        teamClone.setTeam_Name(team.getTeam_Name());
        teamClone.setRank(team.getRank());
        teamClone.setEditable(team.isEditable());
        teamClone.setState(State.Accepted);
        teamClone.setContest(superC);
        teamClone.setMembers(new ArrayList<>(team.getMembers()));
        teamClone.setCloneSource(team);
        teamClone.setIsClone(true);


        return teamRepo.save(teamClone);
    }
}
