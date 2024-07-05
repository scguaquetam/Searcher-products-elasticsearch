package com.unir.facets.data;

import com.unir.facets.model.db.Element;
import com.unir.facets.model.response.AggregationDetails;
import com.unir.facets.model.response.ElementsQueryResponse;
import com.unir.facets.utils.Consts;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final ElasticsearchOperations elasticClient;

    private final String[] title_fields = {"Title", "Title._2gram", "Title._3gram"};
    private final String[] description_fields = {"Description", "Description._2gram", "Description._3gram"};
    @SneakyThrows
    public ElementsQueryResponse findProducts(
            List<String> typeValues,
            List<String> directorValues,
            List<String> releaseYearValues,
            List<String> durationValues,
            List<String> ratingValues,
            String description,
            String title,
            String page
    ) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        // Si el usuario ha seleccionado algun valor relacionado con el tipo, lo añadimos a la query
        if (typeValues != null && !typeValues.isEmpty()) {
            typeValues.forEach(
                    type -> querySpec.must(QueryBuilders.termQuery(Consts.FIELD_TYPE, type))
            );
        }
        // Si el usuario ha seleccionado algun valor relacionado con el director, lo añadimos a la query
        if (directorValues != null && !directorValues.isEmpty()) {
            directorValues.forEach(
                    director -> querySpec.must(QueryBuilders.termQuery(Consts.FIELD_DIRECTOR, director))
            );
        }
        // Si el usuario ha seleccionado algun valor relacionado con el director, lo añadimos a la query
        if (directorValues != null && !directorValues.isEmpty()) {
            directorValues.forEach(
                    director -> querySpec.must(QueryBuilders.termQuery(Consts.FIELD_DIRECTOR, director))
            );
        }

        // Si el usuario ha seleccionado algun valor relacionado con el title, lo añadimos a la query
        if (!StringUtils.isEmpty(title)) {
            querySpec.must(QueryBuilders.multiMatchQuery(title, title_fields).type(MultiMatchQueryBuilder.Type.BOOL_PREFIX));
        }
        // Si el usuario ha seleccionado algun valor relacionado con la descripción, lo añadimos a la query
        if (!StringUtils.isEmpty(description)) {
            querySpec.must(QueryBuilders.multiMatchQuery(description, description_fields).type(MultiMatchQueryBuilder.Type.BOOL_PREFIX));
        }

        // Si el usuario ha seleccionado algun valor relacionado con el año de lanzamiento, lo añadimos a la query
        if (releaseYearValues != null && !releaseYearValues.isEmpty()) {
            releaseYearValues.forEach(
                    releaseYear -> {
                        String[] releaseYearRange = releaseYear != null && releaseYear.contains("-") ? releaseYear.split("-") : new String[]{};

                        if (releaseYearRange.length == 2) {
                            if ("".equals(releaseYearRange[0])) {
                                querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_RELEASE_YEAR).to(releaseYearRange[1]).includeUpper(false));
                            } else {
                                querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_RELEASE_YEAR).from(releaseYearRange[0]).to(releaseYearRange[1]).includeUpper(false));
                            }
                        } if (releaseYearRange.length == 1) {
                            querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_RELEASE_YEAR).from(releaseYearRange[0]));
                        }
                    }
            );
        }

        // Si el usuario ha seleccionado algun valor relacionado con la duracion, lo añadimos a la query
        if (durationValues != null && !durationValues.isEmpty()) {
            durationValues.forEach(
                    duration -> {
                        String[] durationRange = duration != null && duration.contains("-") ? duration.split("-") : new String[]{};

                        if (durationRange.length == 2) {
                            if ("".equals(durationRange[0])) {
                                querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_DURATION).to(durationRange[1]).includeUpper(false));
                            } else {
                                querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_DURATION).from(durationRange[0]).to(durationRange[1]).includeUpper(false));
                            }
                        } if (durationRange.length == 1) {
                            querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_DURATION).from(durationRange[0]));
                        }
                    }
            );
        }

        // Si el usuario ha seleccionado algun valor relacionado con el rating, lo añadimos a la query
        if (ratingValues != null && !ratingValues.isEmpty()) {
            ratingValues.forEach(
                    rating -> {
                        String[] ratingRange = rating != null && rating.contains("-") ? rating.split("-") : new String[]{};

                        if (ratingRange.length == 2) {
                            if ("".equals(ratingRange[0])) {
                                querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_RATING).to(ratingRange[1]).includeUpper(false));
                            } else {
                                querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_RATING).from(ratingRange[0]).to(ratingRange[1]).includeUpper(false));
                            }
                        } if (ratingRange.length == 1) {
                            querySpec.must(QueryBuilders.rangeQuery(Consts.FIELD_RATING).from(ratingRange[0]));
                        }
                    }
            );
        }

        //Si no se ha seleccionado ningun filtro, se añade un filtro por defecto para que la query no sea vacia
        if(!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        //Construimos la query
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        //Se incluyen las Agregaciones
        //Se incluyen las agregaciones de termino para los campos director y type
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .terms(Consts.AGG_KEY_TERM_TYPE)
                .field(Consts.FIELD_TYPE).size(10000));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                    .terms(Consts.AGG_KEY_TERM_DIRECTOR)
                    .field(Consts.FIELD_DIRECTOR).size(10000));

        //Se incluyen las agregaciones de rango para los campos release year, Duration y rating
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range(Consts.AGG_KEY_RANGE_RELEASE)
                .field(Consts.FIELD_RELEASE_YEAR)
                .addUnboundedTo(Consts.AGG_KEY_RANGE_RELEASE_0,2000)
                .addRange(Consts.AGG_KEY_RANGE_RELEASE_1, 2000, 2010)
                .addUnboundedFrom(Consts.AGG_KEY_RANGE_RELEASE_2,2010));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range(Consts.AGG_KEY_RANGE_DURATION)
                .field(Consts.FIELD_DURATION)
                .addUnboundedTo(Consts.AGG_KEY_RANGE_DURATION_0,100)
                .addRange(Consts.AGG_KEY_RANGE_DURATION_1,100, 150)
                .addUnboundedFrom(Consts.AGG_KEY_RANGE_DURATION_2,150));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range(Consts.AGG_KEY_RANGE_RATING)
                .field(Consts.FIELD_RATING)
                .addUnboundedTo(Consts.AGG_KEY_RANGE_RATING_0,5)
                .addRange(Consts.AGG_KEY_RANGE_RATING_1,5, 8)
                .addUnboundedFrom(Consts.AGG_KEY_RANGE_RATING_2,8));

        //Se establece un maximo de 5 resultados, va acorde con el tamaño de la pagina
        nativeSearchQueryBuilder.withMaxResults(5);

        //Podemos paginar los resultados en base a la pagina que nos llega como parametro
        //El tamaño de la pagina es de 5 elementos (pero el propio llamante puede cambiarlo si se habilita en la API)
        int pageInt = Integer.parseInt(page);
        if (pageInt >= 0) {
            nativeSearchQueryBuilder.withPageable(PageRequest.of(pageInt,5));
        }

        //Se construye la query
        Query query = nativeSearchQueryBuilder.build();
        // Se realiza la busqueda
        SearchHits<Element> result = elasticClient.search(query, Element.class);
        return new ElementsQueryResponse(getResponseElements(result), getResponseAggregations(result));
    }

    /**
     * Metodo que convierte los resultados de la busqueda en una lista de empleados.
     * @param result Resultados de la busqueda.
     * @return Lista de empleados.
     */
    private List<Element> getResponseElements(SearchHits<Element> result) {
        return result.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    /**
     * Metodo que convierte las agregaciones de la busqueda en una lista de detalles de agregaciones.
     * Se ha de tener en cuenta que el tipo de agregacion puede ser de tipo rango o de tipo termino.
     * @param result Resultados de la busqueda.
     * @return Lista de detalles de agregaciones.
     */
    private Map<String, List<AggregationDetails>> getResponseAggregations(SearchHits<Element> result) {

        //Mapa de detalles de agregaciones
        Map<String, List<AggregationDetails>> responseAggregations = new HashMap<>();

        //Recorremos las agregaciones
        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();

            //Recorremos las agregaciones
            aggs.forEach((key, value) -> {

                //Si no existe la clave en el mapa, la creamos
                if(!responseAggregations.containsKey(key)) {
                    responseAggregations.put(key, new LinkedList<>());
                }

                //Si la agregacion es de tipo termino, recorremos los buckets
                if (value instanceof ParsedStringTerms parsedStringTerms) {
                    parsedStringTerms.getBuckets().forEach(bucket -> {
                        responseAggregations.get(key).add(new AggregationDetails(bucket.getKey().toString(), (int) bucket.getDocCount()));
                    });
                }

                //Si la agregacion es de tipo rango, recorremos tambien los buckets
                if (value instanceof ParsedRange parsedRange) {
                    parsedRange.getBuckets().forEach(bucket -> {
                        responseAggregations.get(key).add(new AggregationDetails(bucket.getKeyAsString(), (int) bucket.getDocCount()));
                    });
                }
            });
        }
        return responseAggregations;
    }
}
