package org.fh.gae.query.index.memory.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfo {
    private String tid;

    private String token;

    private Integer type;

    private AuthStatus status = AuthStatus.NORMAL;
}
