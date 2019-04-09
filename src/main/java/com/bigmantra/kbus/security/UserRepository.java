package com.bigmantra.kbus.security;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;



@RepositoryRestResource(collectionResourceRel = "users",path = "users")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  public User findByUsername(String username);

}
