package com.nmquan1503.backend_springboot.repositories.support;

import com.nmquan1503.backend_springboot.entities.support.SupportRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    Page<SupportRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<SupportRequest> findByReadOrderByCreatedAtDesc(boolean read, Pageable pageable);
    long countByReadFalse();
}
