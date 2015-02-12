package com.kariqu.categorycenter.domain.model.tree;

import java.io.Serializable;

/**
 * @Author: Tiger
 * @Since: 11-6-25 下午3:07
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface TreeModelType<IdType extends Serializable> {

    IdType getId();
}
