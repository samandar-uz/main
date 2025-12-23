package org.example.fasthost.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TzpayResponse {
    private boolean success;
    private String id;
    private String redirect_url;
}
