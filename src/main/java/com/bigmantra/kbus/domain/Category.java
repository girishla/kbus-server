package com.bigmantra.kbus.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "expensecategory")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Category extends AbstractKbusObject {

	private String name;
	private String color;
	private Long userId;
	private Long groupId;
	private String icon;



}