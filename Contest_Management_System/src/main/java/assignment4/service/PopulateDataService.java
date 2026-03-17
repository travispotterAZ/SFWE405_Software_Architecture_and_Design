package assignment4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import assignment4.model.Contest;
import assignment4.model.Team;
import assignment4.model.Person;
import assignment4.model.Person.Role;
import assignment4.data.ContestRepository;
import assignment4.data.TeamRepository;
import assignment4.data.PersonRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class PopulateDataService {

    @Autowired
    private ContestRepository contestRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private PersonRepository personRepo;

    @Transactional
    public void populateData() {

        // ------------------ Main Contest ------------------
        Contest mainContest = new Contest();
        mainContest.setName("UofA_Programming_Contest");
        mainContest.setCapacity(10);
        mainContest.setEditable(true);
        contestRepo.save(mainContest); // Persist main contest first

        // Manager for Main Contest
        Person manager = new Person();
        manager.setRole(Person.Role.Manager);
        manager.setName("Hal Tharp");
        manager.setEmail("halTharp@uofa.edu");
        manager.getManaged_contests().add(mainContest); // link manager → contest
        personRepo.save(manager);

        // ------------------ Sub-Contest ------------------
        Contest subContest = new Contest();
        subContest.setName("UofA-SWFE405_Final");
        subContest.setCapacity(5);
        subContest.setEditable(true);
        contestRepo.save(subContest);

        // Manager for Sub-Contest
        Person manager1 = new Person();
        manager1.setRole(Person.Role.Manager);
        manager1.setName("Tomas Cerny");
        manager1.setEmail("tomasCerny@uofa.edu");
        manager1.getManaged_contests().add(subContest);
        personRepo.save(manager1);

        // Link Sub-Contest as Preliminary Round
        mainContest.setPreliminary_round(subContest);
        contestRepo.save(mainContest);

        // ------------------ Coaches ------------------
        Person coach1 = new Person();
        coach1.setRole(Person.Role.Coach);
        coach1.setName("Coach Smith");
        personRepo.save(coach1);

        Person coach2 = new Person();
        coach2.setRole(Person.Role.Coach);
        coach2.setName("Coach Johnson");
        personRepo.save(coach2);

        Person coach3 = new Person();
        coach3.setRole(Person.Role.Coach);
        coach3.setName("Coach Lee");
        personRepo.save(coach3);

        // ------------------ Students ------------------
        Person alice   = new Person("Alice",   LocalDate.of(2005, 6, 10), Role.Student);
        Person bob     = new Person("Bob",     LocalDate.of(2004, 8, 15), Role.Student);
        Person charlie = new Person("Charlie", LocalDate.of(2003, 12, 20), Role.Student);
        personRepo.saveAll(List.of(alice, bob, charlie));

        Person david = new Person("David", LocalDate.of(2005, 2, 5), Role.Student);
        Person eve   = new Person("Eve",   LocalDate.of(2004, 4, 22), Role.Student);
        Person frank = new Person("Frank", LocalDate.of(2003, 9, 30), Role.Student);
        personRepo.saveAll(List.of(david, eve, frank));

        Person grace = new Person("Grace", LocalDate.of(2005, 1, 18), Role.Student);
        Person heidi = new Person("Heidi", LocalDate.of(2004, 5, 25), Role.Student);
        Person ivan  = new Person("Ivan",  LocalDate.of(2003, 10, 12), Role.Student);
        personRepo.saveAll(List.of(grace, heidi, ivan));


        //Will be used to make a team & then add to contest in endpoints
        Person James = new Person("James", LocalDate.of(2004, 11, 16), Role.Student);
        Person Beth = new Person("Beth", LocalDate.of(2005, 5, 4), Role.Student);
        Person Bennett = new Person("Bennett", LocalDate.of(2004, 8, 29), Role.Student);
        Person CoachDavey = new Person("Coach Davey", LocalDate.of(1995, 9, 6), Role.Coach);
        personRepo.save(James);
        personRepo.save(Beth);
        personRepo.save(Bennett);
        personRepo.save(CoachDavey);

        Person Random1 = new Person("Random1", LocalDate.of(2004, 11, 16), Role.Student);
        Person Random2 = new Person("Random2", LocalDate.of(2005, 5, 4), Role.Student);
        Person Random3 = new Person("Random3", LocalDate.of(2004, 8, 29), Role.Student);
        Person RandomCoach = new Person("RandomCoach", LocalDate.of(1995, 9, 6), Role.Coach);
        personRepo.save(Random1);
        personRepo.save(Random2);
        personRepo.save(Random3);
        personRepo.save(RandomCoach);
        


        // ------------------ Teams ------------------
        Team team1 = new Team();
        team1.setTeam_Name("Bulldogs");
        team1.setContest(subContest);
        team1.setMembers(List.of(alice, bob, charlie));
        teamRepo.save(team1);
        coach1.getCoached_teams().add(team1);
        personRepo.save(coach1);

        Team team2 = new Team();
        team2.setTeam_Name("Tigers");
        team2.setContest(subContest);
        team2.setMembers(List.of(david, eve, frank));
        teamRepo.save(team2);
        coach2.getCoached_teams().add(team2);
        personRepo.save(coach2);

        Team team3 = new Team();
        team3.setTeam_Name("Bears");
        team3.setContest(subContest);
        team3.setMembers(List.of(grace, heidi, ivan));
        teamRepo.save(team3);
        coach3.getCoached_teams().add(team3);
        personRepo.save(coach3);
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> populateData();
    }
}
