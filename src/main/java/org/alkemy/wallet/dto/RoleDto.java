package org.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.alkemy.wallet.model.RoleName;

import java.io.Serializable;
import java.util.Date;

@Data
public class RoleDto implements Serializable {

    private Long id;
    private RoleName roleName;
    private String description;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date creationDate;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date updateDate;

}
