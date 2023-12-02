package project.vegist.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_value")
    private Integer unitValue;

    @Column(name = "unit_name")
    private String unitName;

    // other fields, getters, setters, and constructors
}

