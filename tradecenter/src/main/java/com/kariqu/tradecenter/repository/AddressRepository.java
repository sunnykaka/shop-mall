package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.Address;

import java.util.List;

/**
 * User: Asion
 * Date: 12-5-30
 * Time: 下午5:50
 */
public interface AddressRepository {

    void createAddress(Address address);

    void updateAddress(Address address);

    void deleteAddress(int addressId);

    void deleteAddress(int addressId, int userId);

    Address getAddress(int addressId);

    List<Address> queryAddressByUserId(int userId);

    void updateDefaultAddressState(int addressId);

    void updateNotDefaultAddressState(int userId, int addressId);

    Address queryDefaultAddress(int userId);

    void updateFrequencyByAddressId(int id);

    Address getAddress(int addressId,int userId);
}
