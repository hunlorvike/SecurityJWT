package project.vegist.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "actions")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_name", unique = true)
    private String actionName;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private List<UserAction> userActions;
}
