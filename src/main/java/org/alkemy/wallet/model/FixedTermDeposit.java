package org.alkemy.wallet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@Table(name = "FIXED_TERM_DEPOSITS")
public class FixedTermDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FIXED_TERM_DEPOSIT_ID")
    private Long fixedTermDepositsId;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account accountId;

    @Column(name = "INTEREST", nullable = false)
    private Double interest;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @Column(name = "CLOSING_DATE")
    private Date closingDate;

}
