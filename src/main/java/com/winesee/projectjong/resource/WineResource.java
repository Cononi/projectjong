package com.winesee.projectjong.resource;

import com.winesee.projectjong.domain.wine.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WineResource {

    @GetMapping("find/source")
    public ResponseEntity<Country> searchCountry(String country){

        return null;
    }
}
