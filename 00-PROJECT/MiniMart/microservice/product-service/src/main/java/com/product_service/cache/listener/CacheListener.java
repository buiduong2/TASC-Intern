package com.product_service.cache.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.product_service.event.CategoryEvent;
import com.product_service.event.ProductEvent;
import com.product_service.event.TagEvent;
import com.product_service.repository.CategoryRepository;
import com.product_service.repository.ProductRepository;
import com.product_service.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheListener {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final TagRepository tagRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationProductChange(ProductEvent productEvent) {

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationCategoryChange(CategoryEvent categoryEvent) {

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationTagChange(TagEvent tagEvent) {

    }

}
