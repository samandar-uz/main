package org.example.fasthost.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private boolean success;
private String id;
    private String redirectUrl;
}
