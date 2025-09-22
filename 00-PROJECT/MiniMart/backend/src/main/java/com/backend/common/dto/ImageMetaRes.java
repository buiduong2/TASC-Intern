package com.backend.common.dto;

import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Transactional
public class ImageMetaRes {
    private String publicId;
    private String url;
}
