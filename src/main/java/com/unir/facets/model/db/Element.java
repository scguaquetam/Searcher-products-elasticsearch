package com.unir.facets.model.db;

import com.unir.facets.utils.Consts;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(indexName = "elements", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Element {
    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = Consts.FIELD_TYPE)
    private String type;

    @Field(type = FieldType.Search_As_You_Type, name = Consts.FIELD_TITLE)
    private String title;

    @Field(type = FieldType.Keyword, name = Consts.FIELD_POSTER)
    private String poster;

    @Field(type = FieldType.Search_As_You_Type, name = Consts.FIELD_DESCRIPTION)
    private String description;

    @Field(type = FieldType.Keyword, name = Consts.FIELD_DIRECTOR)
    private String director;

    @Field(type = FieldType.Integer, name = Consts.FIELD_RELEASE_YEAR)
    private String releaseYear;

    @Field(type = FieldType.Integer, name = Consts.FIELD_DURATION)
    private String duration;

    @Field(type = FieldType.Double, name = Consts.FIELD_RATING)
    private String rating;

    @Field(type = FieldType.Keyword, name = Consts.FIELD_TRAILERID)
    private String trailerId;
}
