package de.keithpaterson.javersrollbackproblem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class JaversRollbackProblemApplicationTestsSimpleCommit {

	Logger logger = LoggerFactory.getLogger(getClass());


	@Autowired
	Javers javers;

	@Autowired
	JaversRepo rJaversRepo;

	@Test
	void contextLoads() {
	}

	@Test
	void loadOne() {
		for (int i = 0; i<120; i++) {
			Customer c = new Customer();
			c.setId(i);
			c.setFirstName("BLA_"+i);
			javers.commit("null", c);
		}
		for (int i = 200; i<320; i++) {
			Customer c = new Customer();
			c.setId(i);
			c.setFirstName("BLUBB_"+i);
			javers.commit("null", c);
		}
		Changes c2 = javers.findChanges(QueryBuilder.anyDomainObject().build());
		assertEquals(300, c2.size());
		logger.info(()-> javers.getJsonConverter().toJson(c2).toString());
	}


	@Transactional(Transactional.TxType.REQUIRES_NEW) 
	public void addTooLong() 
	{
		for (int i = 0; i<3000; i++) {
			Customer c = new Customer();
			c.setFirstName("BLA" + i);
			rJaversRepo.save(c);
		}
		try {
			for (int i = 0; i<10; i++) {
				Customer c = new Customer();
				c.setFirstName("FarTooLongFirstName");
				rJaversRepo.save(c);
				//Triggers Commit
				rJaversRepo.count();
			}				
		} catch (Throwable e) {
			logger.info(e, null);
		}
	}
}
