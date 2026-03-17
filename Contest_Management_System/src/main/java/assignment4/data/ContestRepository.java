package assignment4.data;

import org.springframework.data.jpa.repository.JpaRepository;
import assignment4.model.Contest;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Contest findByName(String name);

}
