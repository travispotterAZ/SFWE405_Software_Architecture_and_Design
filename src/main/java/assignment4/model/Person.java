package assignment4.model;

import java.util.ArrayList;
import java.time.LocalDate;                      //for date of birth field
import java.time.Period;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;          //for JPA entity mapping
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;              //for primary key annotation
import jakarta.persistence.GeneratedValue;  //for auto-generating primary key values
import jakarta.validation.constraints.*;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "person")
public class Person{
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Name cannot be blank") //valifdation for name field
    private String name;

    private String email;
    private String university;

    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private Role role;
    public enum Role {
        Student, Coach, Manager
    }

    //Manager
    @ManyToMany
    private List<Contest> managed_contests = new ArrayList<>();

    //Coach
    @OneToMany              //One Person ---> Many Teams (as Coaches)
    private List<Team> coached_teams = new ArrayList<>();

    @JsonIgnore
    public int getAge(){  //needed for checking age req, and used for reports
        int age = Period.between(this.birthdate, LocalDate.now()).getYears();
        return age;
    }

    //Contructors
    public Person(){}

    public Person(String name, LocalDate birthdate, Role role) { //Contructor with birthdate
        this.name = name;
        this.birthdate = birthdate;
        this.role = role;
    }

}
