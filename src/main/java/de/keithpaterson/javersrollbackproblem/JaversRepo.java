package de.keithpaterson.javersrollbackproblem;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
@JaversSpringDataAuditable
public interface JaversRepo extends CrudRepository<Customer, Long>{

}
