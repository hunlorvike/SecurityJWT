package hung.learn.securityjwt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
    public String getPrefixedName() {
        // Check if the role name starts with "ROLE_", and add the prefix if it doesn't
        if (!name.startsWith("ROLE_")) {
            return "ROLE_" + name;
        }
        return name;
    }
}

/*
* ADMIN
* USER
* ADMIN_READ
* ADMIN_POST
* ADMIN_UPDATE
* ADMIN_DELETE
*/