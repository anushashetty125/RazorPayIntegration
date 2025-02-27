package com.rp.RazorPayIntegration.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeyResponse {
    private String key;
}
