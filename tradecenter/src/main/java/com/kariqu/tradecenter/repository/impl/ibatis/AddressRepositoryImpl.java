package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.Address;
import com.kariqu.tradecenter.repository.AddressRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: wendy
 * Date: 12-5-31
 * Time: 上午9:35
 */
public class AddressRepositoryImpl extends SqlMapClientDaoSupport implements AddressRepository {

    @Override
    public void createAddress(Address address) {
        getSqlMapClientTemplate().insert("insertAddress", address);
    }

    @Override
    public void updateAddress(Address address) {
        getSqlMapClientTemplate().update("updateAddress", address);
    }

    @Override
    public void deleteAddress(int addressId) {
        getSqlMapClientTemplate().update("deleteAddress", addressId);
    }

    @Override
    public void deleteAddress(int addressId, int userId) {
        Map map = new HashMap();
        map.put("id",addressId);
        map.put("userId",userId);
        getSqlMapClientTemplate().update("deleteAddressByIdAndUserId", map);
    }

    @Override
    public Address getAddress(int addressId) {
        return (Address) getSqlMapClientTemplate().queryForObject("selectAddressById", addressId);
    }

    @Override
    public Address getAddress(int addressId, int userId) {
        Map map = new HashMap();
        map.put("id",addressId);
        map.put("userId",userId);
        return (Address) getSqlMapClientTemplate().queryForObject("selectAddressByIdAndUserId", map);
    }

    @Override
    public List<Address> queryAddressByUserId(int userId) {
        return getSqlMapClientTemplate().queryForList("selectAddressByUserId", userId);
    }

    @Override
    public void updateDefaultAddressState(int addressId) {
        getSqlMapClientTemplate().update("updateDefaultAddressById", addressId);
    }

    @Override
    public void updateNotDefaultAddressState(int userId, int addressId) {
        Map map =new HashMap();
        map.put("userId",userId);
        map.put("id",addressId);
        getSqlMapClientTemplate().update("updateDefaultAddress", map);
    }

    @Override
    public Address queryDefaultAddress(int userId) {
        return (Address) getSqlMapClientTemplate().queryForObject("queryDefaultAddress", userId);
    }

    @Override
    public void updateFrequencyByAddressId(int id) {
        getSqlMapClientTemplate().update("updateFrequencyByAddressId", id);
    }

}
