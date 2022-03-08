package com.winesee.projectjong.domain.wine;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name="wine")
public class Wine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 와인 번호
    private Long wine_id;

    private String display_name;

    private String display_name_ko;

    private String grape_list;

    private String producer_name;

    private String wine;

    private String country;

    private String region;

    private String sub_region;

    private String colour;

    private String type;

    private String sub_type;

    private String degignation;

    private String classification;

}
