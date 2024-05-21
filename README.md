# javers-rollback-problem

This project demonstrates a problem within Javers. There are a few tests using Javers and demonstrating the basic usage.

JaversRollbackProblemApplicationTestsBackupRestore.java uses an edge case we require. The database is backed up and restored. To perform the restore all sequences are reset to their previous value. For test purposes basically all tables are deleted and the sequences reset. 

Playing with      
  ```private static final long SEQUENCE_ALLOCATION_SIZE = 1;```
will show the problem. With 
```private static final long SEQUENCE_ALLOCATION_SIZE = 100;```
the tests will fail since ```org.javers.repository.sql.session.Sequence.currentValue``` still has a value and we get a non linear sequence that fails.

