package com.winesee.projectjong.domain.wine;

import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
public class Country implements Serializable {

    @Id
    @Column(name="countryId")
    private Long countryId;

    private String country;
}
