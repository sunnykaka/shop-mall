package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.Address;
import com.kariqu.tradecenter.repository.AddressRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-10-13
 * Time: 上午10:41
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class AddressRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("addressRepository")
    private AddressRepository addressRepository;


    @Test
    public void testAddressRepository() {
        Address address=new Address();
        address.setUserId(1);
        address.setDefaultAddress(true);
        address.setLocation("深圳市宝安区大宝路");
        address.setMobile("135656");
        address.setTelephone("66666666");
        address.setName("收货人");
        address.setZipCode("1111111");
        address.setEmail("8522@qq.com");
        address.setProvince("广东省");
        address.setFrequency(1);

        addressRepository.createAddress(address);
        addressRepository.createAddress(address);
        addressRepository.createAddress(address);
        addressRepository.createAddress(address);
        addressRepository.createAddress(address);
        addressRepository.updateNotDefaultAddressState(address.getUserId(),address.getId());
        assertEquals(true,addressRepository.getAddress(address.getId()).isDefaultAddress());
        assertEquals(5,addressRepository.queryAddressByUserId(address.getUserId()).size());
        assertEquals(true, addressRepository.getAddress(address.getId()).isDefaultAddress());
        addressRepository.updateDefaultAddressState(address.getId());
        assertEquals(true, addressRepository.getAddress(address.getId()).isDefaultAddress());

        Address add=new Address();
        add.setId(address.getId());
        add.setUserId(1);
        add.setDefaultAddress(true);
        add.setLocation("深圳市龙岗区大宝路");
        add.setMobile("135656");
        add.setTelephone("66666666");
        add.setName("收货人");
        add.setZipCode("1111111");
        add.setEmail("8522@qq.com");
        add.setProvince("广东省");
        add.setFrequency(6);

        addressRepository.updateAddress(add);

        List<Address> addresses = addressRepository.queryAddressByUserId(address.getUserId());
        assertEquals(5,addresses.size());
        assertEquals(6, addresses.get(0).getFrequency());
        addressRepository.updateFrequencyByAddressId(address.getId());
        assertEquals(7,addressRepository.getAddress(address.getId()).getFrequency());
        addressRepository.deleteAddress(address.getId());
        assertEquals(null,addressRepository.getAddress(address.getId()));
        assertEquals(4,addressRepository.queryAddressByUserId(1).size());
    }
}
