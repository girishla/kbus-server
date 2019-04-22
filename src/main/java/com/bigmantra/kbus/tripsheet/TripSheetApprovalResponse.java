package com.bigmantra.kbus.tripsheet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripSheetApprovalResponse {

	private Long busSummaryId;
	private boolean isApproved = true;
	private String salesReceiptId;
	private String expenseId;

}

