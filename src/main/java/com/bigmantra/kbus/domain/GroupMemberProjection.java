package com.bigmantra.kbus.domain;

import com.bigmantra.kbus.security.User;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "groupMemberProjection", types = { Member.class })
public interface GroupMemberProjection {

    String getId();
    Group getGroup();
    User getUser();

}


