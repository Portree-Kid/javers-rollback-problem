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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class JaversRollbackProblemApplicationTests {

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
			c.setFirstName("BLA");
			rJaversRepo.save(c);
		}
		assertEquals(120, rJaversRepo.count());
	}


	@Test
	void loadTwo() {
		for (int i = 0; i<120; i++) {
			Customer c = new Customer();
			c.setFirstName("BLA");
			rJaversRepo.save(c);
		}
		assertEquals(120, rJaversRepo.count());
	}

	@Test
	void loadThree() {
		for (int i = 0; i<120; i++) {
			Customer c = new Customer();
			c.setFirstName("BLA");
			rJaversRepo.save(c);
		}
		assertEquals(120, rJaversRepo.count());
		addTooLong();
		Changes c = javers.findChanges(QueryBuilder.anyDomainObject().build());
		assertEquals(300, c.size());
		logger.info(()-> javers.getJsonConverter().toJson(c).toString());
	}


	@Test
	void loadFour() {
		for (int i = 0; i<120; i++) {
			Customer c = new Customer();
			c.setFirstName("BLA");
			rJaversRepo.save(c);
		}
		assertEquals(120, rJaversRepo.count());
		Changes c = javers.findChanges(QueryBuilder.anyDomainObject().build());
		assertEquals(300, c.size());
		logger.info(()-> javers.getJsonConverter().toJson(c).toString());
	}

	@Test
	void loadFive() {
		for (int i = 0; i<120; i++) {
			Customer c = new Customer();
			c.setFirstName("BLA");
			rJaversRepo.save(c);
		}
		assertEquals(120, rJaversRepo.count());
		Changes c = javers.findChanges(QueryBuilder.anyDomainObject().build());
		assertEquals(300, c.size());
		logger.info(()-> javers.getJsonConverter().toJson(c).toString());
	}

	@Test
	void loadSix() {
		for (int i = 0; i<120; i++) {
			Customer c = new Customer();
			c.setFirstName("BLA");
			rJaversRepo.save(c);
		}
		assertEquals(120, rJaversRepo.count());
		Changes c = javers.findChanges(QueryBuilder.anyDomainObject().build());
		assertEquals(300, c.size());
		logger.info(()-> javers.getJsonConverter().toJson(c).toString());
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
