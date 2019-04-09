package com.bigmantra.kbus.security;


import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class KbusUserFactory {

  public static KbusUserDetails create(User user) {
    Collection<GrantedAuthority> authorities;
    try {
      authorities =  AuthorityUtils.commaSeparatedStringToAuthorityList(user.getAuthorities());
    } catch (Exception e) {
      authorities = null;
    }
    return new KbusUserDetails(
      user.getId(),
      user.getUsername(),
      user.getPassword(),
      user.getEmail(),
      user.getLastPasswordReset(),
       authorities
    );
  }

}

