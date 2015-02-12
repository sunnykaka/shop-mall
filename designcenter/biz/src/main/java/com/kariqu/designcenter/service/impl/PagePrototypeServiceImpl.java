package com.kariqu.designcenter.service.impl;

import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.domain.model.prototype.PrototypeState;
import com.kariqu.designcenter.repository.PagePrototypeRepository;
import com.kariqu.designcenter.service.InitRenderService;
import com.kariqu.designcenter.service.PagePrototypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Tiger
 * @version 1.0
 * @since 12-4-20 上午11:04
 */
public class PagePrototypeServiceImpl implements PagePrototypeService {

    @Autowired
    private PagePrototypeRepository pagePrototypeRepository;

    @Autowired
    private InitRenderService initRenderService;

    @Override
    public PagePrototype getPagePrototypeById(int id) {
        return pagePrototypeRepository.getPagePrototypeById(id);
    }

    @Override
    public List<PagePrototype> queryAllPagePrototype() {
        return pagePrototypeRepository.queryAllPagePrototype();
    }

    @Override
    public void createPagePrototype(PagePrototype pagePrototype) {
        pagePrototypeRepository.createPagePrototype(pagePrototype);
    }

    @Override
    public void updatePagePrototype(PagePrototype pagePrototype) {
        pagePrototypeRepository.updatePagePrototype(pagePrototype);
    }

    @Override
    public void deletePagePrototype(int id) {
        pagePrototypeRepository.deletePagePrototypeById(id);
    }

    @Override
    public void releasePagePrototypeId(int pagePrototypeId) {
        PagePrototype pagePrototype = getPagePrototypeById(pagePrototypeId);
        pagePrototype.setPrototypeState(PrototypeState.RELEASE);
        String prototypePageConfig = initRenderService.initRenderPagePrototype(pagePrototype);
        pagePrototype.setConfigContent(prototypePageConfig);
        updatePagePrototype(pagePrototype);
    }


}
