package com.publicissapient.kpidashboard.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.publicissapient.kpidashboard.common.model.tracelog.JobExecutionTraceLog;
import com.publicissapient.kpidashboard.common.repository.tracelog.JobExecutionTraceLogRepository;

@ExtendWith(MockitoExtension.class)
class JobExecutionTraceLogServiceImplTest {

	@Mock
	private JobExecutionTraceLogRepository jobExecutionTraceLogRepository;

	@InjectMocks
	private JobExecutionTraceLogServiceImpl jobExecutionTraceLogService;

	private JobExecutionTraceLog traceLog;
	private ObjectId logId;

	@BeforeEach
	void setUp() {
		logId = new ObjectId();
		traceLog = new JobExecutionTraceLog();
		traceLog.setId(logId);
		traceLog.setProcessorName("TestProcessor");
		traceLog.setJobName("TestJob");
		traceLog.setExecutionOngoing(false);
		traceLog.setExecutionSuccess(true);
		traceLog.setExecutionStartedAt(Instant.now());
	}

	@Test
	void testCreateProcessorJobExecution() {
		when(jobExecutionTraceLogRepository.save(any(JobExecutionTraceLog.class))).thenReturn(traceLog);

		JobExecutionTraceLog result =
				jobExecutionTraceLogService.createProcessorJobExecution("TestProcessor", "TestJob");

		assertNotNull(result);
		assertEquals(logId, result.getId());
		assertEquals("TestProcessor", result.getProcessorName());
		assertEquals("TestJob", result.getJobName());

		verify(jobExecutionTraceLogRepository).save(any(JobExecutionTraceLog.class));
	}

	@Test
	void testUpdateJobExecution() {
		when(jobExecutionTraceLogRepository.save(traceLog)).thenReturn(traceLog);

		jobExecutionTraceLogService.updateJobExecution(traceLog);

		verify(jobExecutionTraceLogRepository).save(traceLog);
	}

	@Test
	void testFindById() {
		when(jobExecutionTraceLogRepository.findById(logId)).thenReturn(Optional.of(traceLog));

		Optional<JobExecutionTraceLog> result = jobExecutionTraceLogService.findById(logId);

		assertTrue(result.isPresent());
		assertEquals(traceLog, result.get());
		verify(jobExecutionTraceLogRepository).findById(logId);
	}

	@Test
	void testFindByIdNotFound() {
		when(jobExecutionTraceLogRepository.findById(logId)).thenReturn(Optional.empty());

		Optional<JobExecutionTraceLog> result = jobExecutionTraceLogService.findById(logId);

		assertFalse(result.isPresent());
		verify(jobExecutionTraceLogRepository).findById(logId);
	}

	@Test
	void testFindLastExecutionsByProcessorAndJobName() {
		List<JobExecutionTraceLog> traceLogs = Arrays.asList(traceLog);
		when(jobExecutionTraceLogRepository.findLastExecutionTraceLogsByProcessorAndJobName("TestProcessor", "TestJob",
				PageRequest.ofSize(5))).thenReturn(traceLogs);

		List<JobExecutionTraceLog> result = jobExecutionTraceLogService
				.findLastExecutionsByProcessorAndJobName("TestProcessor", "TestJob", 5);

		assertEquals(traceLogs, result);
		verify(jobExecutionTraceLogRepository).findLastExecutionTraceLogsByProcessorAndJobName("TestProcessor", "TestJob",
				PageRequest.ofSize(5));
	}

	@Test
	void testIsJobCurrentlyRunningTrue() {
		traceLog.setExecutionOngoing(true);
		List<JobExecutionTraceLog> traceLogs = Arrays.asList(traceLog);
		when(jobExecutionTraceLogRepository.findLastExecutionTraceLogsByProcessorAndJobName("TestProcessor", "TestJob",
				PageRequest.ofSize(1))).thenReturn(traceLogs);

		boolean result = jobExecutionTraceLogService.isJobCurrentlyRunning("TestProcessor", "TestJob");

		assertTrue(result);
	}

	@Test
	void testIsJobCurrentlyRunningFalse() {
		traceLog.setExecutionOngoing(false);
		List<JobExecutionTraceLog> traceLogs = Arrays.asList(traceLog);
		when(jobExecutionTraceLogRepository.findLastExecutionTraceLogsByProcessorAndJobName("TestProcessor", "TestJob",
				PageRequest.ofSize(1))).thenReturn(traceLogs);

		boolean result = jobExecutionTraceLogService.isJobCurrentlyRunning("TestProcessor", "TestJob");

		assertFalse(result);
	}

	@Test
	void testIsJobCurrentlyRunningEmptyList() {
		when(jobExecutionTraceLogRepository.findLastExecutionTraceLogsByProcessorAndJobName(
						"TestProcessor", "TestJob", PageRequest.ofSize(1)))
				.thenReturn(Collections.emptyList());

		boolean result = jobExecutionTraceLogService.isJobCurrentlyRunning("TestProcessor", "TestJob");

		assertFalse(result);
	}
}
