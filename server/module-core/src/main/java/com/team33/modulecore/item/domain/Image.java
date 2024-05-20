package com.team33.modulecore.item.domain;

import javax.persistence.Embeddable;

import groovy.transform.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Embeddable
public class Image {

    private String thumbnail;

    private String descriptionImage;

    public Image(String thumbnail, String descriptionImage) {
        this.thumbnail = thumbnail;
        this.descriptionImage = descriptionImage;
    }
}
