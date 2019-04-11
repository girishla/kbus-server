package com.bigmantra.kbus.domain;

import com.bigmantra.kbus.security.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "usergroupmember")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Member extends AbstractKbusObject {

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "groupid")
	Group group;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	User user;

	private boolean isAccepted = true;

}