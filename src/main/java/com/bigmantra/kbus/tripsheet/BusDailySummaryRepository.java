package com.bigmantra.kbus.tripsheet;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.*;


@RepositoryRestResource(collectionResourceRel = "busdailysummaries",path = "busdailysummaries")
@Repository
public interface BusDailySummaryRepository extends JpaRepository<BusDailySummary, Long> {


    public List<BusDailySummary> findBySubmittedById(Long userId);

    public List<BusDailySummary> findByGroupIdAndDateIdBetween(Long groupId,Long startDateId, Long endDateId);
    public List<BusDailySummary> findByGroupId(Long groupId);
    public List<BusDailySummary> findByConductorId(Long conductorId);
    public List<BusDailySummary> findBydriverId(Long driverId);




}
