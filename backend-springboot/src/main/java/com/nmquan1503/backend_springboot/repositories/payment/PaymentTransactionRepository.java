package com.nmquan1503.backend_springboot.repositories.payment;

import com.nmquan1503.backend_springboot.entities.payment.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PaymentTransaction> findByTransactionId(String transactionId);

}
