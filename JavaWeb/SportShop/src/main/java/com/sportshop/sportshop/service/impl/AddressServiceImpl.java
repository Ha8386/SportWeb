package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.entity.AddressEntity;
import com.sportshop.sportshop.repository.AddressRepository;
import com.sportshop.sportshop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public AddressEntity save(AddressEntity address) {
        return addressRepository.save(address);
    }

    @Override
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }
} 