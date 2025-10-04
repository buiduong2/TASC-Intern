package com.admin_bff.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.util.MultiValueMap;

import com.admin_bff.dto.res.UserSummaryDTO;
import com.common.dto.PageResponseDTO;

public interface UserService {

    CompletableFuture<PageResponseDTO<UserSummaryDTO>> getUsers(MultiValueMap<String, String> params);

}
