package com.bigmantra.kbus.domain;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;


@RepositoryRestResource(collectionResourceRel = "groups",path = "groups")
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {




}
