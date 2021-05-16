package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.OrderLine;
import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    List<OrderLine> findAllByPurchaseStatus(PurchaseStatus purchaseStatus);
    void deleteAllByPurchaseStatus(PurchaseStatus purchaseStatus);
}
