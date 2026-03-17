package assignment4.model;

import java.util.Date;

import jakarta.persistence.Entity;          //for JPA entity mapping
import jakarta.persistence.Id;              //for primary key annotation
import jakarta.persistence.GeneratedValue;  //for auto-generating primary key values

//import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;

import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "contest")
public class Contest{

    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private int capacity;
    private Date date = new Date();
    private boolean registration_allowed;
    private Date registration_from = new Date();
    private Date registration_to = new Date();
    private boolean editable;

    @ManyToOne                                              //Many Contests ---> One Contest (as Preliminary Round)
    private Contest preliminary_round;
}
