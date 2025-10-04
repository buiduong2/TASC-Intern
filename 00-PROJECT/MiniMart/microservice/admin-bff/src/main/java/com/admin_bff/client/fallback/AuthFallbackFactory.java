package com.admin_bff.client.fallback;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.admin_bff.client.AuthClient;
import com.admin_bff.dto.res.UserAuthDTO;
import com.common.dto.PageResponseDTO;

@Component
public class AuthFallbackFactory implements FallbackFactory<AuthClient> {

    private static final Logger log = LoggerFactory.getLogger(AuthFallbackFactory.class);

    @Override
    public AuthClient create(Throwable cause) {
        log.error("FALLBACK Kích hoạt: Auth Service không khả dụng: ", cause);
        return new AuthClient() {

            // @Override
            // public CompletableFuture<PageResponseDTO<UserAuthDTO>> getUsers(Map<String,
            // ?> params) {
            // Object pageObject = params.get("page");
            // Object sizeObject = params.get("size");

            // String pageStr = pageObject != null ? pageObject.toString() : "0";
            // String sizeStr = sizeObject != null ? sizeObject.toString() : "20";

            // int page = Integer.parseInt(pageStr);
            // int size = Integer.parseInt(sizeStr);
            // return CompletableFuture.completedFuture(PageResponseDTO.empty(page, size));
            // }

            @Override
            public PageResponseDTO<UserAuthDTO> getUsers(Map<String, ?> params) {
                Object pageObject = params.get("page");
                Object sizeObject = params.get("size");

                String pageStr = pageObject != null ? pageObject.toString() : "0";
                String sizeStr = sizeObject != null ? sizeObject.toString() : "20";

                int page = Integer.parseInt(pageStr);
                int size = Integer.parseInt(sizeStr);
                return PageResponseDTO.empty(page, size);
            }

        };
    }

}
