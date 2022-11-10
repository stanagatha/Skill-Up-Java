package org.alkemy.wallet.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.alkemy.wallet.model.Currency;

@Data
public class AccountDto implements Serializable {

    private Long id;
    private Currency currency;
    private Double transactionLimit;
    private Double balance;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date creationDate;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date updateDate;
    private Boolean softDelete;

}
