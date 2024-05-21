package de.keithpaterson.javersrollbackproblem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import jakarta.transaction.Transactional;
import org.w3c.dom.Entity;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Transactional
public class JaversRollbackProblemApplicationTestsBackupRestore {

	Logger logger = LoggerFactory.getLogger(getClass());


	@PersistenceContext
	EntityManager em;

	@Autowired
	Javers javers;

	@Autowired
	JaversRepo rJaversRepo;


    @Order(1)
	@Test
	void loadOne() {
		for (int i = 0; i<199; i++) {
			Customer c = new Customer();
			c.setId(i);
			c.setFirstName("BLA_"+i);
			javers.commit("null", c);
		}
		Changes c2 = javers.findChanges(QueryBuilder.anyDomainObject().withSnapshotType(SnapshotType.INITIAL).limit(1000).build());
		assertEquals(597, c2.size());
		logger.info(()-> javers.getJsonConverter().toJson(c2).toString());
	}


	@Order(2)
	@Test
	void loadTwo() {
		restore();
		for (int i = 0; i<300; i++) {
			Customer c = new Customer();
			c.setId(i);
			c.setFirstName("BLA_"+i);
			javers.commit("null", c);
		}
	}

	/**
	 * We perform a DB Restore. This involves resetting the Database to it's previous state. In this case it's emtpy
	 * jv_global_id - domain object identifiers,
	 * jv_commit - JaVers commits metadata,
	 * jv_commit_property - commit properties,
	 * jv_snapshot - domain object snapshots.
	 *
	 * jv_commit_pk_seq
	 * jv_global_id_pk_seq
	 */
	private void restore() {
        em.createNativeQuery("DELETE FROM CUSTOMER").executeUpdate();
		em.createNativeQuery("DELETE FROM jv_global_id").executeUpdate();
		em.createNativeQuery("DELETE FROM jv_commit").executeUpdate();
		em.createNativeQuery("DELETE FROM jv_commit_property").executeUpdate();
		em.createNativeQuery("DELETE FROM jv_snapshot").executeUpdate();

        em.createNativeQuery("alter sequence jv_commit_pk_seq  restart with 1");
		em.createNativeQuery("alter sequence jv_global_id_pk_seq  restart with 1");

	}


}
