package com.bigmantra.kbus.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;


@Data
public class SimpleGrantedAuthority implements GrantedAuthority {

    private String authority;


}
