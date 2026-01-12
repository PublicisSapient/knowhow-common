package com.publicissapient.kpidashboard.common.repository.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.publicissapient.kpidashboard.common.model.scm.User;

@ExtendWith(SpringExtension.class)
public class ScmUserRepositoryCustomImplTest {

	@Mock
	private MongoOperations operations;

	@Mock
	private MongoCollection<Document> mongoCollection;

	@Mock
	private AggregateIterable<Document> aggregateIterable;

	@Mock
	private MongoCursor<Document> mongoCursor;

	@Mock
	private MongoConverter mongoConverter;

	@InjectMocks
	private ScmUserRepositoryCustomImpl repository;

	private BasicDBList filterList;
	private User user;

	@BeforeEach
	void setUp() {
		filterList = new BasicDBList();
		filterList.add(new BasicDBObject("username", "test"));

		user = new User();
		user.setUsername("test123");
		user.setEmail("test@example.com");
	}

	@Test
	void testFindScmUserList_Success() {
		Document document = new Document("username", "test123");

		when(operations.getCollection("scm_users")).thenReturn(mongoCollection);
		when(mongoCollection.aggregate(anyList())).thenReturn(aggregateIterable);
		when(aggregateIterable.iterator()).thenReturn(mongoCursor);
		when(mongoCursor.hasNext()).thenReturn(true, false);
		when(mongoCursor.next()).thenReturn(document);
		when(operations.getConverter()).thenReturn(mongoConverter);
		when(mongoConverter.read(eq(User.class), any(Document.class))).thenReturn(user);

		List<User> result = repository.findScmUserList(filterList);

		assertEquals(1, result.size());
		assertEquals("test123", result.get(0).getUsername());
	}

	@Test
	void testFindScmUserList_EmptyFilterList() {
		List<User> result = repository.findScmUserList(new BasicDBList());
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindScmUserList_NullFilterList() {
		List<User> result = repository.findScmUserList(null);
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindScmUserList_NoResults() {
		when(operations.getCollection("scm_users")).thenReturn(mongoCollection);
		when(mongoCollection.aggregate(anyList())).thenReturn(aggregateIterable);
		when(aggregateIterable.iterator()).thenReturn(mongoCursor);
		when(mongoCursor.hasNext()).thenReturn(false);

		List<User> result = repository.findScmUserList(filterList);

		assertTrue(result.isEmpty());
	}

	@Test
	void testFindScmUserList_MongoCommandException() {
		when(operations.getCollection("scm_users")).thenReturn(mongoCollection);
		when(mongoCollection.aggregate(anyList())).thenReturn(aggregateIterable);
		when(aggregateIterable.iterator())
				.thenThrow(new MongoCommandException(new BsonDocument(), null));

		List<User> result = repository.findScmUserList(filterList);

		assertTrue(result.isEmpty());
	}
}
