package com.winesee.projectjong.domain.wine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name="wine")
@NoArgsConstructor
public class Wine implements Serializable {

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

    // 생산나라20
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country", referencedColumnName="countryId")
    private Country country;

    // 밭
    private String region;

    private String subRegion;

    private String colour;

    private String type;

    private String subType;

    private String designation;

    private String classification;

    private String wineImageUrl;

    private String contents;

    // 알콜 도수
    private int alcohol;

    // 최종 토탈 점수
    private int averageScore;


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Wine)) {
            return false;
        }
        Wine input = (Wine) obj;
        return this.toString().equals(input.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.toString());
    }
}
