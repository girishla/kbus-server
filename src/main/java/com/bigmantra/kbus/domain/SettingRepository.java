package com.bigmantra.kbus.domain;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "settings",path = "settinga")
@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {




}
