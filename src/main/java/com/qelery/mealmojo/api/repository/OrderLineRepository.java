package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.OrderLine;
import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    List<OrderLine> findAllByPurchaseStatusAndUserId(PurchaseStatus purchaseStatus, Long userId);
    Optional<OrderLine> findAllByPurchaseStatusAndUserIdAndMenuItemId(PurchaseStatus purchaseStatus, Long userId, Long menuItemId);
    void deleteAllByPurchaseStatusAndUserId(PurchaseStatus purchaseStatus, Long userId);
}
