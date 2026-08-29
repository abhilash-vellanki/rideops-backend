package com.abhilash.rideops.dto;

import java.math.BigDecimal;

public record PlatformCommissionSummaryDTO(BigDecimal totalCommission, long transactionCount) {
}
