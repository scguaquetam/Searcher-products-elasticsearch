package com.unir.facets.controller;

import com.unir.facets.model.response.ElementsQueryResponse;
import com.unir.facets.service.ElementsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ElementsController {

    private final ElementsService service;

    @GetMapping("/elements")
    public ResponseEntity<ElementsQueryResponse> getProducts(
            @RequestParam(required = false) List<String> typeValues,
            @RequestParam(required = false) List<String> directorValues,
            @RequestParam(required = false) List<String> releaseYearValues,
            @RequestParam(required = false) List<String> durationValues,
            @RequestParam(required = false) List<String> ratingValues,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "0") String page) {

        ElementsQueryResponse response = service.getProducts(
                typeValues,
                directorValues,
                releaseYearValues,
                durationValues,
                ratingValues,
                description,
                title,
                page
        );
        return ResponseEntity.ok(response);
    }
}
