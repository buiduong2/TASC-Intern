package com.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortMetadataDTO {

    private boolean sorted;

    private boolean unsorted;

    private boolean empty;

    private List<SortOrderDTO> orders;
}