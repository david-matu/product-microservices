package com.david.microservices.alpha.review;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.david.microservices.alpha.review.persistence.ReviewEntity;
import com.david.microservices.alpha.review.persistence.ReviewRepository;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PersistenceTest extends MySqlTestBase {
	
	@Autowired
	private ReviewRepository repository;
	
	private ReviewEntity savedEntity;
	
	@BeforeEach
	void setupDb() {
		repository.deleteAll();
		
		ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c"); // new ReviewEntity(1, 2, "AuthorA", "Subject S", "Content c");
		savedEntity = repository.save(entity);
		
		assertEqualsReview(entity, savedEntity);
	}
	
	@Test
	void create() {
		ReviewEntity newEntity = new ReviewEntity(1, 3, "a", "s", "c");
		repository.save(newEntity);
		
		ReviewEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsReview(newEntity, foundEntity);
		
		assertEquals(2, repository.count());
	}
	
	@Test
	void update() {
		savedEntity.setAuthor("a2");
		repository.save(savedEntity);
		
		ReviewEntity foundEntity = repository.findById(savedEntity.getId()).get();
		assertEquals(1, (long)foundEntity.getVersion());
		assertEquals("a2", foundEntity.getAuthor());
	}
	
	@Test
	void delete() {
		repository.delete(savedEntity);
		assertFalse(repository.existsById(savedEntity.getId()));
	}
	
	@Test
	void getByProductId() {
		List<ReviewEntity> entityList = repository.findByProductId(savedEntity.getProductId());
		
		assertThat(entityList, hasSize(1));
		assertEqualsReview(savedEntity, entityList.get(0));
	}
	
	@Test
	void duplicateError() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c");
			repository.save(entity);
		});
	}
	
	@Test
	void optimisticLockError() {
		
		// Store savedEntity in two separate entities
		ReviewEntity entityA = repository.findById(savedEntity.getId()).get();
		ReviewEntity entityB = repository.findById(savedEntity.getId()).get();
		
		// Update the entity using the first object
		entityA.setAuthor("a1");
		repository.save(entityA);
		
		// The following should throw an exception since we are updating the object with stale data (indicated by version), that's Optimistic Lock Error
		assertThrows(OptimisticLockingFailureException.class, () -> {
			entityB.setAuthor("a2");
			repository.save(entityB);
		});
		
		// Get the updated entity from the database and verify its new state
		ReviewEntity updatedEntity = repository.findById(savedEntity.getId()).get();
		
		assertEquals(1, (int)updatedEntity.getVersion());
		assertEquals("a1", updatedEntity.getAuthor());
		
	}

	private void assertEqualsReview(ReviewEntity expectedEntity, ReviewEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
		assertEquals(expectedEntity.getReviewId(), actualEntity.getReviewId());
		assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
		assertEquals(expectedEntity.getSubject(), actualEntity.getSubject());
		assertEquals(expectedEntity.getContent(), actualEntity.getContent());
	}
}
