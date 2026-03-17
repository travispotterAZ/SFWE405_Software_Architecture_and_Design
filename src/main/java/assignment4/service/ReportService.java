package assignment4.service;

import org.springframework.beans.factory.annotation.Autowired; //Spring
import org.springframework.stereotype.Service;

import assignment4.data.PersonRepository;  //Objects & Repositories
import assignment4.model.Person;

import java.time.LocalDate; //Date handling for Age Calculation
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Service
public class ReportService {
    
    @Autowired
    private PersonRepository personRepo;

    public String reportStudentsByAge() {
        List<Person> students = personRepo.findByRole(Person.Role.Student); //Get all students from repository

        Map<Integer, Long> ageGroups = new HashMap<>(); //Map for age, count of students at age

		for (Person p : students) {
			// Convert birthdate to LocalDate
			LocalDate birthDate = p.getBirthdate();

			 int age = Period.between(birthDate, LocalDate.now()).getYears(); //Calulating age in years

			ageGroups.put(age, ageGroups.getOrDefault(age, 0L) + 1); //adgust count for age group
		}

        StringBuilder report = new StringBuilder();

        report.append("Report: Number of Students Grouped by Age\n");
        report.append("Age | Count\n");
        ageGroups.keySet().stream()
            .sorted()
            .forEach(age -> report.append(age + " | " + ageGroups.get(age) + "\n"));


        return report.toString();
    }
}