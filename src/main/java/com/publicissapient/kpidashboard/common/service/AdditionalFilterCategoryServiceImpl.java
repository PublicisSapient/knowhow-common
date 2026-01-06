package com.publicissapient.kpidashboard.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.application.AdditionalFilterCategory;
import com.publicissapient.kpidashboard.common.repository.application.AdditionalFilterCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdditionalFilterCategoryServiceImpl implements AdditionalFilterCategoryService {

	private final AdditionalFilterCategoryRepository additionalFilterCategoryRepository;

	@Override
	public List<AdditionalFilterCategory> getAdditionalFilterCategories() {
		return additionalFilterCategoryRepository.findAllByOrderByLevel();
	}
}
