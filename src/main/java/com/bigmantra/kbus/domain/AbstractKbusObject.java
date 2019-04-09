package com.bigmantra.kbus.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.util.Date;

/**
 * Base class for entity implementations. Uses a {@link Long} id.
 * 
 * @author Girish lakshmanan
 */
@MappedSuperclass
@Getter
@ToString
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class AbstractKbusObject implements Identifiable<Long> {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private @Version
    Long version;

	protected AbstractKbusObject() {
		this.id = null;
	}
	
	public void setId(Long id) {
		this.id=id;
	}

	
	public void setVersion(Long version) {
		this.version=version;
	}


	@Override
	public Long getId() {
		return this.id;
	}


	@Column(name = "createdDate", updatable = false,columnDefinition = "DATE")
	@CreatedDate
	private Date  createdDate;


	@Column(name = "updatedDate",columnDefinition = "DATE")
	@LastModifiedDate
	private Date  modifiedDate;

}
