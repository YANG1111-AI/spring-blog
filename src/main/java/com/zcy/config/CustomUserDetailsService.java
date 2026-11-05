package com.zcy.config;

import com.zcy.domain.Authority;
import com.zcy.domain.User;
import com.zcy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private IUserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getAuthorities() != null) {
            for (Authority authority : user.getAuthorities()) {
                authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
            }
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getValid() == 1,
                true,
                true,
                true,
                authorities
        );
    }
}