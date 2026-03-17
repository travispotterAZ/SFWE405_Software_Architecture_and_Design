package assignment4.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import assignment4.model.Person;
import assignment4.model.Team;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findAll();

    List<Person> findByRole(Person.Role role);

    @Query("SELECT p FROM Person p JOIN p.coached_teams t WHERE t = :team AND p.role = assignment4.model.Person.Role.Coach")
    List<Person> findCoachesForTeam(Team team);

    Person findCoachById(Long id);

}
