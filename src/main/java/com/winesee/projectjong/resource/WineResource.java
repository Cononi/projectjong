package com.winesee.projectjong.resource;

import com.winesee.projectjong.domain.wine.Country;
import com.winesee.projectjong.domain.wine.CountryRepository;
import com.winesee.projectjong.service.wine.WineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class WineResource {

    private final WineService wineService;

    @GetMapping(value = "find/country", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchCountry(){
        List<Country> list = wineService.wineCountryResult();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
