package com.ecommerce.system.seller.service.domain.outbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderEventPayload {

    @JsonProperty
    private String orderId;

    @JsonProperty
    private String sellerId;

    @JsonProperty
    private ZonedDateTime createdAt;

    @JsonProperty
    private String orderApprovalStatus;

    @JsonProperty
    private List<String> failureMessages;


}
