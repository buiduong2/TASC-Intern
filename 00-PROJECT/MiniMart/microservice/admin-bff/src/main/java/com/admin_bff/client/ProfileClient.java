package com.admin_bff.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.admin_bff.dto.res.ProfileDTO;

@FeignClient("profile-service")
public interface ProfileClient {

    @PostMapping("/v1/admin/profiles/by-ids")
    // CompletableFuture<Map<Long, ProfileDTO>> getProfileByIds(@RequestBody
    // List<Long> ids);
    Map<Long, ProfileDTO> getProfileByIds(@RequestBody List<Long> ids);
}
