package com.winesee.projectjong.resource;

import com.winesee.projectjong.domain.wine.Country;
import com.winesee.projectjong.domain.wine.CountryRepository;
import com.winesee.projectjong.domain.wine.Search;
import com.winesee.projectjong.service.wine.WineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "find/wine", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> wineListMain(@RequestBody Search search) throws IllegalAccessException {
        if(search.getQuery().length() < 2){
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(wineService.wineAll(search), HttpStatus.OK);
    }

}
