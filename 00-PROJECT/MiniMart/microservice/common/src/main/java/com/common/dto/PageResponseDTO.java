package com.common.dto;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {

    private List<T> content;

    private long totalElements;

    private int totalPages;

    private int pageNumber;

    private int pageSize;

    private boolean last;

    private boolean first;

    private boolean empty;

    private SortMetadataDTO sort;

    public PageResponseDTO(List<T> content, long totalElements, int totalPages,
            int pageNumber, int pageSize, boolean last, boolean empty) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.last = last;
        this.empty = empty;
    }

    public static <T, S> PageResponseDTO<T> from(
            PageResponseDTO<S> source,
            List<T> newContent) {

        return new PageResponseDTO<>(
                newContent,
                source.getTotalElements(),
                source.getTotalPages(),
                source.getPageNumber(),
                source.getPageSize(),
                source.isLast(),
                source.isFirst(),
                newContent.isEmpty(),
                source.getSort());
    }

    public static <T, S> PageResponseDTO<T> empty(PageResponseDTO<S> source) {
        return from(source, Collections.emptyList());
    }

    public static <T> PageResponseDTO<T> empty(int pageNumber, int pageSize) {

        return new PageResponseDTO<>(
                Collections.emptyList(),
                0,
                0,
                pageNumber,
                pageSize,
                true,
                true,
                true,
                null);
    }
}