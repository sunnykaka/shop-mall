package com.kariqu.tradecenter.service.impl;

import com.kariqu.tradecenter.domain.InvoiceCompany;
import com.kariqu.tradecenter.repository.InvoiceCompanyRepository;
import com.kariqu.tradecenter.service.InvoiceCompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Wendy
 * Date: 11-10-14
 * Time: 下午3:08
 */
public class InvoiceCompanyServiceImpl implements InvoiceCompanyService {

    @Autowired
    private InvoiceCompanyRepository invoiceCompanyRepository;

    @Override
    public void createInvoiceCompany(InvoiceCompany invoiceCompany) {
        invoiceCompanyRepository.createInvoiceCompany(invoiceCompany);
    }

    @Override
    public List<InvoiceCompany> queryUserInvoiceCompany(int userId) {
        return invoiceCompanyRepository.queryUserInvoiceCompany(userId);
    }

    @Override
    public void updateInvoiceCompany(InvoiceCompany invoiceCompany) {
        invoiceCompanyRepository.updateInvoiceCompany(invoiceCompany);
    }

    @Override
    public void deleteInvoiceCompany(int id) {
       invoiceCompanyRepository.deleteInvoiceCompany(id);
    }
}
