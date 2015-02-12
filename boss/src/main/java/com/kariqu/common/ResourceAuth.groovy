package com.kariqu.common

/**
 * User: Asion
 * Date: 11-11-20
 * Time: 下午4:06
 */
class ResourceAuth {

    def uiAuth(resource, account, role, roleScope) {
        def name = resource.getName()
        def value = roleScope.getScopeValue()
        def split = value.split("--")
        List list = split.toList()
        list.contains(name)
    }

}
