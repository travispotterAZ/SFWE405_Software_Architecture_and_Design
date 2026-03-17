package assignment4.data;

import org.springframework.data.jpa.repository.JpaRepository;
import assignment4.model.Team;
import assignment4.model.Contest;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByContestName(String contestName);

    List<Team> findByContest(Contest contest);
}
