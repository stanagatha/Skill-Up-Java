package org.alkemy.wallet.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ROLES")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private long id;

    @Column(name = "ROLE_NAME", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @Column(name = "DESCRIPT")
    private String description;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "UPDATE_DATE")
    private Date updateDate;
}
