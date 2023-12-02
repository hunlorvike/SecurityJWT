package project.vegist.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "labels")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "label_name", unique = true)
    private String labelName;

    @OneToMany(mappedBy = "label", cascade = CascadeType.ALL)
    private List<Product> products;

}
