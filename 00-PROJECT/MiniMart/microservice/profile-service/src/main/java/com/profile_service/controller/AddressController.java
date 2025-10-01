package com.profile_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profile_service.dto.req.AddressCreateReq;
import com.profile_service.dto.res.AddressInfo;
import com.profile_service.security.CustomUserDetails;
import com.profile_service.service.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    @GetMapping("{id}")
    public AddressInfo getById(@PathVariable long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.findByIdAndUserId(id, userDetails.getId());
    }

    @PostMapping("profile/{profileId}")
    public AddressInfo create(@PathVariable long profileId, @Valid @RequestBody AddressCreateReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.create(profileId, req, userDetails.getId());
    }

    @PutMapping("{id}")
    public AddressInfo update(@PathVariable long id, @Valid @RequestBody AddressCreateReq req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.update(id, req, userDetails.getId());
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        service.delete(id, userDetails.getId());
    }

}
