package com.publicissapient.kpidashboard.common.repository.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

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
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.publicissapient.kpidashboard.common.model.scm.ScmCommits;

@ExtendWith(SpringExtension.class)
public class ScmCommitRepositoryCustomImplTest {

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
	private ScmCommitRepositoryCustomImpl repository;

	private BasicDBList filterList;
	private ScmCommits scmCommit;

	@BeforeEach
	void setUp() {
		filterList = new BasicDBList();
		filterList.add(new BasicDBObject("processorItemId", "test"));

		scmCommit = new ScmCommits();
		scmCommit.setSha("abc123");
	}

	@Test
	void testFindCommitList_Success() {
		Document document = new Document("sha", "abc123");

		when(operations.getCollection("scm_commit_details")).thenReturn(mongoCollection);
		when(mongoCollection.aggregate(anyList())).thenReturn(aggregateIterable);
		when(aggregateIterable.iterator()).thenReturn(mongoCursor);
		when(mongoCursor.hasNext()).thenReturn(true, false);
		when(mongoCursor.next()).thenReturn(document);
		when(operations.getConverter()).thenReturn(mongoConverter);
		when(mongoConverter.read(eq(ScmCommits.class), any(Document.class))).thenReturn(scmCommit);

		List<ScmCommits> result = repository.findCommitList(1L, 2L, filterList);

		assertEquals(1, result.size());
		assertEquals("abc123", result.get(0).getSha());
	}

	@Test
	void testFindCommitList_EmptyFilterList() {
		List<ScmCommits> result = repository.findCommitList(1L, 2L, new BasicDBList());
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindCommitList_NullFilterList() {
		List<ScmCommits> result = repository.findCommitList(1L, 2L, null);
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindCommitList_NoResults() {
		when(operations.getCollection("scm_commit_details")).thenReturn(mongoCollection);
		when(mongoCollection.aggregate(anyList())).thenReturn(aggregateIterable);
		when(aggregateIterable.iterator()).thenReturn(mongoCursor);
		when(mongoCursor.hasNext()).thenReturn(false);

		List<ScmCommits> result = repository.findCommitList(1L, 2L, filterList);

		assertTrue(result.isEmpty());
	}
}
