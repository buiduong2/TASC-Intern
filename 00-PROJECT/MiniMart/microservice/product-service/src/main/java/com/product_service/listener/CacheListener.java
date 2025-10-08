package com.product_service.listener;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.product_service.event.Action;
import com.product_service.event.CategoryEvent;
import com.product_service.event.ProductEvent;
import com.product_service.event.TagEvent;
import com.product_service.utils.CacheEvictor;
import com.product_service.utils.ProductCacheEvictor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheListener {

    private final ProductCacheEvictor productCacheEvictor;

    private final CacheEvictor cacheEvictor;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationProductChange(ProductEvent event) {
        long productId = event.getId();
        long categoryId = event.getCategoryId();
        Action action = event.getAction();
        if (action == Action.CREATED) {
            return;
        }

        if (action == Action.DELETED) {
            cacheEvictor.evictProductDetail(productId);
            cacheEvictor.evictRelateIdsByProduct(productId);
            cacheEvictor.evictProductIdByCategory(categoryId);
            cacheEvictor.evictProductRelateDTO(productId);
            return;
        }

        if (action == Action.UPDATED) {
            cacheEvictor.evictProductDetail(productId);

            cacheEvictor.evictProductRelateDTO(productId);
            if (event.getCategoryId() != event.getOldCategoryId()) {
                cacheEvictor.evictProductIdByCategory(categoryId);
                cacheEvictor.evictRelateIdsByProduct(productId);
            }
        }

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationCategoryChange(CategoryEvent event) {
        Action action = event.getAction();
        cacheEvictor.evictCategorySummaryList();
        if (action == Action.CREATED) {
            return;
        }

        if (action == Action.UPDATED) {
            cacheEvictor.evictCategoryDetail(event.getId());
            return;
        }

        if (action == Action.DELETED) {
            cacheEvictor.evictProductIdByCategory(event.getId());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationTagChange(TagEvent event) {
        Action action = event.getAction();
        if (action != Action.DELETED && action != Action.UPDATED) {
            return;
        }
        List<Long> productIds = event.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        productCacheEvictor.addDirtyProductIdByTagChange(productIds);
    }

}
