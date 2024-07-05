package com.unir.facets.model.response;

import com.unir.facets.model.db.Element;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ElementsQueryResponse {

    private List<Element> elements;
    private Map<String, List<AggregationDetails>> aggs;

}
