package org.dpr.mykeys.keystore;


import java.util.List;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.keystore.ServiceException;

public  interface StoreService<T extends NodeInfo> {

	 
	List<? extends ChildInfo> getChildList() throws ServiceException;
	

}