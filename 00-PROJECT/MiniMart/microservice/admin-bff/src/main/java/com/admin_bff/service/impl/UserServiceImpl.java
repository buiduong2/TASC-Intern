package com.admin_bff.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.admin_bff.client.AuthClient;
import com.admin_bff.client.OrderClient;
import com.admin_bff.client.ProfileClient;
import com.admin_bff.dto.res.ProfileDTO;
import com.admin_bff.dto.res.UserAuthDTO;
import com.admin_bff.dto.res.UserSummaryDTO;
import com.admin_bff.mapper.UserMapper;
import com.admin_bff.service.UserService;
import com.common.dto.PageResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final AuthClient authClient;
    private final ProfileClient profileClient;
    private final OrderClient orderClient;
    private final UserMapper userMapper;
    private final Executor executor;

    @Autowired
    @Lazy
    private UserService self;

    @Override
    public PageResponseDTO<UserSummaryDTO> getUsers(MultiValueMap<String, String> params) {

        CompletableFuture<PageResponseDTO<UserAuthDTO>> authFuture = CompletableFuture
                .supplyAsync(() -> authClient.getUsers(params), executor);

        CompletableFuture<PageResponseDTO<UserSummaryDTO>> resultFuture = authFuture.thenCompose(authRes -> {

            List<Long> userIds = authRes.getContent().stream().map(UserAuthDTO::getUserId).toList();

            if (userIds.isEmpty()) {
                return CompletableFuture.completedFuture(PageResponseDTO.empty(authRes));
            }

            CompletableFuture<Map<Long, ProfileDTO>> profileFuture = CompletableFuture
                    .supplyAsync(() -> profileClient.getProfileByIds(userIds), executor);

            CompletableFuture<Map<Long, Long>> orderFuture = CompletableFuture
                    .supplyAsync(() -> orderClient.getUserOrderCountByIds(userIds), executor);

            return profileFuture.thenCombine(orderFuture, (profiles, orderCounts) -> {
                List<UserSummaryDTO> summaryContent = userMapper.toSummaryDTO(
                        authRes.getContent(),
                        profiles,
                        orderCounts);
                return PageResponseDTO.from(authRes, summaryContent);
            });
        });

        return resultFuture.join();
    }

}
