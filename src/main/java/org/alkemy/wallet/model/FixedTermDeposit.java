package org.alkemy.wallet.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "FIXED_TERM_DEPOSITS")
@Data
public class FixedTermDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private Account account;

    @Column(name = "INTEREST", nullable = false)
    private Double interest;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "CLOSING_DATE")
    private Date closingDate;

}
