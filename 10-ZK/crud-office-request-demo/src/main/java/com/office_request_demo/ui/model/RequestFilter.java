package com.office_request_demo.ui.model;

import com.office_request_demo.entities.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestFilter {

    private String keyword;
    private Status status;
}
