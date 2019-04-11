package com.bigmantra.kbus.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "categories",path = "categories")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public List<Category> findByUserId(Long userid);
    public List<Category> findByGroupId(Long groupid);


}
