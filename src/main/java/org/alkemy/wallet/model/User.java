package org.alkemy.wallet.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "USERS")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private long id;

    @Column(name="FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name="LAST_NAME", nullable = false)
    private String lastName;

    @Column(name="EMAIL", nullable = false)
    private String email;

    @Column(name="USER_PASSWORD", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name="ROLE_ID", nullable = false)
    private Role role;

    @Column(name="CREATION_DATE")
    private Date creationDate;

    @Column(name="UPDATE_DATE")
    private Date updateDate;

    @Column(name="SOFT_DELETE", nullable = false)
    private boolean softDelete;

}
