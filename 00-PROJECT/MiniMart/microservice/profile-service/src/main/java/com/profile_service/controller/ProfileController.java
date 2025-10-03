package com.profile_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.security.InternalHeaderUserDetails;
import com.profile_service.dto.req.ProfileCreateReq;
import com.profile_service.dto.req.ProfileUpdateReq;
import com.profile_service.dto.res.AddressInfo;
import com.profile_service.dto.res.ProfileInfo;
import com.profile_service.service.AddressService;
import com.profile_service.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    private final AddressService addressService;

    @GetMapping
    public ProfileInfo getById( @AuthenticationPrincipal InternalHeaderUserDetails userDetails) {
        return service.findByIdAndUserId( userDetails.getId());
    }

    @PostMapping
    public ProfileInfo create(@Valid @RequestBody ProfileCreateReq req,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetails) {
        return service.create(req, userDetails.getId());
    }

    @PutMapping
    public ProfileInfo update( @Valid @RequestBody ProfileUpdateReq req,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetails) {
        return service.update( req, userDetails.getId());
    }

    @GetMapping("addresses")
    public Page<AddressInfo> findByProfileIdAndUserId(
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetails) {
        return addressService.findByProfileIdAndUserId(userDetails.getId(), pageable);
    }

}
