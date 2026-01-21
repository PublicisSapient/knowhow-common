package com.publicissapient.kpidashboard.common.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.jira.BoardMetadata;
import com.publicissapient.kpidashboard.common.repository.jira.BoardMetadataRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author shunaray
 */
@Service
@RequiredArgsConstructor
public class BoardMetadataServiceImpl implements BoardMetadataService {

	private final BoardMetadataRepository boardMetadataRepository;

	public List<BoardMetadata> findAll() {
		Iterable<BoardMetadata> iterable = boardMetadataRepository.findAll();
		return StreamSupport.stream(iterable.spliterator(), false).toList();
	}
}
