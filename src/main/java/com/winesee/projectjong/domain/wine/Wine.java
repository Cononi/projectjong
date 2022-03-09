package com.winesee.projectjong.domain.wine;

import com.winesee.projectjong.domain.board.Post;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="wine")
public class Wine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 와인 번호
    private Long wineId;

    // 와인 라벨 풀네임
    private String displayName;

    // 와인 라벨 한글네임
    private String displayNameKo;

    // 와인 품종 정보
    private String grapeList;

    // 생산자
    private String producerName;

    // 와인명
    private String wine;

    // 생산나라
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country")
    private Country country;

    // 밭
    private String region;

    private String subRegion;

    private String colour;

    private String type;

    private String subType;

    private String degignation;

    private String classification;

    private String wineImageUrl;

    // 알콜 도수
    private int alcohol;

}
