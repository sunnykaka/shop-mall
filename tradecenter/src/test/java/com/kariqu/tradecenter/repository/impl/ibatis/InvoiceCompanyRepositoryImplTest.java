package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.InvoiceCompany;
import com.kariqu.tradecenter.repository.InvoiceCompanyRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;


/**
 * User: Wendy
 * Date: 11-10-14
 * Time: 下午3:08
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class InvoiceCompanyRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("invoiceCompanyRepository")
    private InvoiceCompanyRepository invoiceCompanyRepository;

    @Test
    public void testInvoiceCompanyRepository() {

        InvoiceCompany invoiceCompany = new InvoiceCompany();
        invoiceCompany.setCompanyName("喀日曲");
        invoiceCompany.setUserId(852234219);
        invoiceCompanyRepository.createInvoiceCompany(invoiceCompany);
        invoiceCompanyRepository.createInvoiceCompany(invoiceCompany);
        invoiceCompanyRepository.createInvoiceCompany(invoiceCompany);
        assertEquals(3, invoiceCompanyRepository.queryUserInvoiceCompany(852234219).size());
        assertEquals(852234219, invoiceCompanyRepository.queryUserInvoiceCompany(invoiceCompany.getUserId()).get(0).getUserId());

        InvoiceCompany invoice = new InvoiceCompany();
        invoice.setId(invoiceCompany.getId());
        invoice.setCompanyName("居尚美");
        invoiceCompany.setUserId(852234219);
        invoiceCompanyRepository.updateInvoiceCompany(invoice);
        assertEquals("居尚美", invoiceCompanyRepository.queryUserInvoiceCompany(invoiceCompany.getUserId()).get(2).getCompanyName());
        invoiceCompanyRepository.deleteInvoiceCompany(invoiceCompany.getId());
        assertEquals(2, invoiceCompanyRepository.queryUserInvoiceCompany(invoiceCompany.getUserId()).size());
        assertEquals(852234219, invoiceCompanyRepository.queryUserInvoiceCompany(invoiceCompany.getUserId()).get(0).getUserId());
    }


}
