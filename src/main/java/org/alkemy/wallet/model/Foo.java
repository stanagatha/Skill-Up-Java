package org.alkemy.wallet.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Getter
@Setter
public class Foo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fooId; 
}
