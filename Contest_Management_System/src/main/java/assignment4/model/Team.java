package assignment4.model;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;          //for JPA entity mapping
import jakarta.persistence.Id;              //for primary key annotation
import jakarta.persistence.GeneratedValue;  //for auto-generating primary key values

import jakarta.persistence.Enumerated;      //Needed for enum mapping of State
import jakarta.persistence.EnumType;

import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "team")
public class Team{
    
    @Id
    @GeneratedValue
    private Long id;

    private String Team_Name;
    private int Rank;

    private boolean editable;

    @Enumerated(EnumType.STRING)
    private State state;
    public enum State {
        Accepted, Pending, Cancelled
    }

    @ManyToOne(fetch = FetchType.EAGER)            //Many Teams ---> One Contest
    private Contest contest;


    @ManyToMany(fetch = FetchType.EAGER)            //One Team ---> Many Persons (as Memebrs)
    @Size(min=1, max=3)
    private List<Person> members = new ArrayList<>();

    @OneToOne
    private Team cloneSource;                                 //One Team --> One Team (as Clone)

    private boolean IsClone = false;


    public void addMember(Person person){
        if(this.members.size() < 3 && person != null){
            this.members.add(person);
        }
    }

    public void setContest(Contest newContest){
       this.contest = newContest;
    }

}
