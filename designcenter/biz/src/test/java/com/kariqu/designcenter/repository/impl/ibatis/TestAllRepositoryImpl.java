package com.kariqu.designcenter.repository.impl.ibatis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Asion
 * @since 2011-5-1 下午03:18:20
 * @version 1.0.0
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TemplateVersionRepositoryTest.class,
    TemplateStyleRepositoryImplTest.class,
    TemplateResourceRepositoryTest.class,
    TemplateRepositoryImplTest.class,
    TemplatePageRepositoryImplTest.class,
    TemplateModuleRepositoryTest.class,
    ShopTemplateRepositoryImplTest.class,
    ShopPageRepositoryTest.class,
    PagePrototypeRepositoryImplTest.class,
    ModuleInstanceParamRepositoryImplTest.class,
    CommonModuleRepositoryImplTest.class
    })
public class TestAllRepositoryImpl {

}
