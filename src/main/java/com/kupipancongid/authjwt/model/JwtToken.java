package com.kupipancongid.authjwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtToken {
    String issuer;
    String subject;
    Date issuedAt;
    Date expireddAt;
}
