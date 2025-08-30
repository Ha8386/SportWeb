package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.service.GetUserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetUserAuthenticationImpl implements GetUserAuthentication {
    @Override
    public UserEntity getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailServiceImpl.CustomUserDetails) {
            return ((UserDetailServiceImpl.CustomUserDetails) auth.getPrincipal()).getUser();
        }
        return null;
    }
} 