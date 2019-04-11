package com.bigmantra.kbus.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "members",path = "members")
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    public List<Member> findByUserId(Long userid);
    public List<Member> findByGroupId(Long groupid);


}
