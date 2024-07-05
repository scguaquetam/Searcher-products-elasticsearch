package com.unir.facets.data;

import com.unir.facets.model.db.Element;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElementRepository extends ElasticsearchRepository<Element, String> {
	
	List<Element> findAll();
}
