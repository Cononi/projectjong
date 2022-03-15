package com.winesee.projectjong.domain.wine;

import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;


public interface CountryRepository extends JpaRepository<Country, String> {

    @Cacheable(cacheNames ="countrys")
    List<Country> findAll();
}
