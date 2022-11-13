package org.alkemy.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

import java.util.Date;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name="FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name="LAST_NAME", nullable = false)
    private String lastName;

    @Email
    @Column(name="EMAIL", nullable = false, unique = true)
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
    private Boolean softDelete;

	public User(String firstName, String lastName, String email, String password, Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.creationDate = new Date();
		this.updateDate = new Date();
		this.softDelete = false;
	}
    
}
