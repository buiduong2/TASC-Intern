package com.product_service.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.product_service.dto.res.ImageMetaRes;
import com.product_service.service.ImageCloudService;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("rawtypes")
@Service
@RequiredArgsConstructor
public class ImageCloudServiceImpl implements ImageCloudService {
    private final ThreadPoolTaskExecutor executor;
    private final Cloudinary cloudinary;

    private final String PUBLIC_ID = "public_id";
    private final String URL = "url";

    private static final String CATEGORY_FOLDER = "/categries/";
    private static final String PRODUCT_FOLDER = "/products/";

    public CompletableFuture<ImageMetaRes> updateImage(MultipartFile file, Long id,
            String prefix) {

        try {
            byte[] bytes = file.getBytes();

            return executor.submitCompletable(updateProductImage(bytes, id, file.getOriginalFilename(), prefix))
                    .thenApply(this::handleUploadSuccessFull)
                    .exceptionally(this::handleUploadFailed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    private Callable<Map> updateProductImage(byte[] file, long id, String fileName, String prefix) {
        String publicId = prefix + "-" + id;
        String folder;
        if (prefix == PRODUCT_PREFIX) {
            folder = PRODUCT_FOLDER;
        } else {
            folder = CATEGORY_FOLDER;
        }
        return upload(file, publicId, folder);
    }

    private Callable<Map> upload(byte[] file, String publicId, String folder) {
        return () -> {
            Map params1 = ObjectUtils.asMap(
                    "use_filename", true,
                    "public_id", publicId,
                    "asset_folder", folder,
                    "overwrite", true);
            return cloudinary.uploader().upload(file, params1);
        };
    }

    private ImageMetaRes handleUploadSuccessFull(Map map) {
        return new ImageMetaRes((String) map.get(PUBLIC_ID), (String) map.get(URL));
    }

    private ImageMetaRes handleUploadFailed(Throwable ex) {
        ex.printStackTrace();
        return null;
    }

    @Override
    @Async
    public void deleteImageForId(String prefix, String publicId) {
        if (publicId == null) {
            return;
        }
        executor.submit(() -> cloudinary.uploader()
                .destroy(publicId,
                        ObjectUtils.emptyMap()));
    }
}
