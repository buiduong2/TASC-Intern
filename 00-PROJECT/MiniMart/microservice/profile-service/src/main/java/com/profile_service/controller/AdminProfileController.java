package com.profile_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profile_service.dto.res.ProfileDTO;
import com.profile_service.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/profiles")
@RequiredArgsConstructor
public class AdminProfileController {

    private final ProfileService service;

    @PostMapping("/by-ids")
    public Map<Long, ProfileDTO> getProfilesByIdsIn(@RequestBody List<Long> ids) {
        return service.getMapByUserIdFromIdIn(ids);
    }
}
