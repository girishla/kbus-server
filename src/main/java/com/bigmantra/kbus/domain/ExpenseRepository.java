package com.bigmantra.kbus.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "expenses",path = "expenses")
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    public List<Expense> findByUserId(Long userId);
    public List<Expense> findByGroupId(Long groupId);
    public List<Expense> findByCategoryId(Long categoryId);


}
