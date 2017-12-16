package org.dpr.mykeys.app.keystore;


import java.util.List;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.ServiceException;

public  interface StoreService<T extends NodeInfo> {

	 
	List<? extends ChildInfo> getChildList() throws ServiceException;
	

}