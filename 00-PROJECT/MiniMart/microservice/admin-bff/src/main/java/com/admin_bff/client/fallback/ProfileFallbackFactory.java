package com.admin_bff.client.fallback;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.admin_bff.client.ProfileClient;
import com.admin_bff.dto.res.ProfileDTO;

@Component
public class ProfileFallbackFactory implements FallbackFactory<ProfileClient> {

    private static final Logger log = LoggerFactory.getLogger(ProfileFallbackFactory.class);

    @Override
    public ProfileClient create(Throwable cause) {
        log.error("ProfileService is unavaiable, ", cause);
        return new ProfileClient() {

            @Override
            public Map<Long, ProfileDTO> getProfileByIds(List<Long> ids) {
                return Collections.emptyMap();
            }

            // @Override
            // public CompletableFuture<Map<Long, ProfileDTO>> getProfileByIds(List<Long>
            // ids) {
            // return CompletableFuture.completedFuture(Collections.emptyMap());
            // }

        };
    }

}
