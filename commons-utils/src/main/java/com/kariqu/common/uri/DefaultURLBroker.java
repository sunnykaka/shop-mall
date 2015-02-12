package com.kariqu.common.uri;

/** 
 * 缺省的实现
 * @author Tiger
 * @since 2011-4-22 下午05:31:10 
 * @version 1.0   
 */
public class DefaultURLBroker extends URLBroker {

	public DefaultURLBroker(URLBroker urlBroker){
		super(urlBroker);
	}

        public DefaultURLBroker() {

        }

	@Override
	public URLBroker newInstance() {
		return new DefaultURLBroker(this);
	}

}
