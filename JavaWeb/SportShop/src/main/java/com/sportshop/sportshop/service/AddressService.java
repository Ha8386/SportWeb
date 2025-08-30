package com.sportshop.sportshop.service;

import com.sportshop.sportshop.entity.AddressEntity;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {
    AddressEntity save(AddressEntity address);
    void deleteById(Long id);
} 