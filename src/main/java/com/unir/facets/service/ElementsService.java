package com.unir.facets.service;

import com.unir.facets.model.response.ElementsQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.unir.facets.data.DataAccessRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElementsService {

	private final DataAccessRepository repository;

	public ElementsQueryResponse getProducts(
			List<String> typeValues,
			List<String> directorValues,
			List<String> releaseYearValues,
			List<String> durationValues,
			List<String> ratingValues,
			String description,
			String title,
			String page
	) {

		return repository.findProducts(
				typeValues,
				directorValues,
				releaseYearValues,
				durationValues,
				ratingValues,
				description,
				title,
				page
		);
	}
}
