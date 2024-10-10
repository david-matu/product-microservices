package com.david.microservices.alpha.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
		
		ReviewEntity entity = new ReviewEntity(1, 2, "AuthorA", "Subject S", "Content c");
		
		savedEntity = repository.save(entity);
		assertEqualsReview(entity, savedEntity);
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
