package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.dto.PlatformCommissionSummaryDTO;
import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.PlatformCommission;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.PlatformCommissionRepository;
import com.abhilash.rideops.services.PlatformCommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformCommissionServiceImpl implements PlatformCommissionService {

    private final PlatformCommissionRepository platformCommissionRepository;

    @Override
    @Transactional
    public PlatformCommission recordCommission(Payment payment, BigDecimal amount) {
        if (payment == null || payment.getId() == null) {
            throw new IllegalArgumentException("A persisted payment is required to record platform commission");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Platform commission amount must be greater than zero");
        }
        if (platformCommissionRepository.existsByPaymentId(payment.getId())) {
            throw new RuntimeConflictException(
                    "Platform commission has already been recorded for paymentId=" + payment.getId()
            );
        }

        PlatformCommission commission = PlatformCommission.builder()
                .payment(payment)
                .amount(amount)
                .build();
        PlatformCommission savedCommission = platformCommissionRepository.save(commission);
        log.info("Platform commission recorded: commissionId={}, paymentId={}, amount={}",
                savedCommission.getId(), payment.getId(), amount);
        return savedCommission;
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformCommissionSummaryDTO getSummary() {
        BigDecimal totalCommission = platformCommissionRepository.sumAmount().orElse(BigDecimal.ZERO);
        return new PlatformCommissionSummaryDTO(totalCommission, platformCommissionRepository.count());
    }
}
