package com.bigmantra.kbus.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "setting")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Setting {


	@NonNull
	@Column(unique=true)
	@Id
	private String name;

	@Column(length=1024)
	private String value;


}