package com.team33.modulecore.item.domain;

import groovy.transform.EqualsAndHashCode;
import javax.persistence.Embeddable;
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
}
