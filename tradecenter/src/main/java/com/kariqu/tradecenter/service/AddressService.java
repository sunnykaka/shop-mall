package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.Address;

import java.util.List;

/**
 * User: Asion
 * Date: 12-5-30
 * Time: 下午5:50
 */
public interface AddressService {

    /**
     * 根据用户ID查询出这个用户的所有地址对象
     *
     * @param userId
     * @return
     */
    List<Address> queryAllAddress(int userId);


    /**
     * 查询某个用户的缺省地址
     *
     * @param userId
     * @return
     */
    Address queryDefaultAddress(int userId);


    /**
     * 创建地址
     *
     * @param address
     */
    void createAddress(Address address);


    /**
     * 更新地址
     *
     * @param address
     */
    void updateAddress(Address address);


    /**
     * 根据地址ID删除地址
     *
     * @param addressId
     */
    void deleteAddress(int addressId);

    /**
     * 根据地址ID 用户ID删除地址
     *
     * @param addressId
     */
    void deleteAddress(int addressId, int userId);

    /**
     * 根据地址ID修改缺省地址
     *
     * @param addressId
     */
    void updateDefaultAddress(int userId, int addressId);

    /**
     * 根据用户Id增加地址使用频率
     */
    void updateFrequencyByAddressId(int id);

    /**
     * 根据地址Id获取详细信息
     */
    Address getAddress(int addressId);

    /**
     * 根据地址Id 用户ID获取详细信息
     */
    Address getAddress(int addressId,int userId);
}
