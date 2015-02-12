package com.kariqu.tradecenter.service.impl;

import com.kariqu.tradecenter.domain.Address;
import com.kariqu.tradecenter.repository.AddressRepository;
import com.kariqu.tradecenter.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: wendy
 * Date: 12-5-31
 * Time: 上午10:25
 */
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;


    @Override
    public List<Address> queryAllAddress(int userId) {
        return addressRepository.queryAddressByUserId(userId);
    }

    @Override
    public Address queryDefaultAddress(int userId) {
        return addressRepository.queryDefaultAddress(userId);
    }

    @Override
    public void createAddress(Address address) {
        addressRepository.createAddress(address);
    }

    @Override
    public void updateAddress(Address address) {
        addressRepository.updateAddress(address);
    }

    @Override
    public void deleteAddress(int addressId) {
        addressRepository.deleteAddress(addressId);
    }

    @Override
    public void deleteAddress(int addressId, int userId) {
        addressRepository.deleteAddress(addressId, userId);
    }

    @Override
    @Transactional
    public void updateDefaultAddress(int userId, int addressId) {
        addressRepository.updateDefaultAddressState(addressId);
        addressRepository.updateNotDefaultAddressState(userId, addressId);
    }

    @Override
    public void updateFrequencyByAddressId(int id) {
        addressRepository.updateFrequencyByAddressId(id);
    }

    @Override
    public Address getAddress(int addressId) {
        return addressRepository.getAddress(addressId);
    }

    @Override
    public Address getAddress(int addressId, int userId) {

        return addressRepository.getAddress(addressId, userId);
    }
}
