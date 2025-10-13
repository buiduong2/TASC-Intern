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
import com.product_service.utils.ProductCacheManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheListener {

    private final ProductCacheManager productCacheManager;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationProductChange(ProductEvent event) {
        long productId = event.getId();
        long categoryId = event.getCategoryId();
        Action action = event.getAction();
        if (action == Action.CREATED) {
            productCacheManager.evictProduct(productId, categoryId);

            return;
        }

        if (action == Action.UPDATED) {
            productCacheManager.putProductById(productId, categoryId);
        }

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationCategoryChange(CategoryEvent event) {
        Action action = event.getAction();
        if (action == Action.CREATED) {
            return;
        }

        if (action == Action.UPDATED) {
            productCacheManager.putCategoryId(event.getId());
            return;
        }

        if (action == Action.DELETED) {
            productCacheManager.evictCategory(event.getId());
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

        productCacheManager.addDirtyProductIdByTagChange(productIds);
    }

}
