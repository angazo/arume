package com.angazo.arume.core.port.company;

import java.util.List;
import java.util.Optional;

import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.FiscalIdentification;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(CompanyId id);

    List<Company> findAll();

    boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification);
}
