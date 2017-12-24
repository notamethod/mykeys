package org.dpr.mykeys.ihm.components;

import java.util.List;

import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreService;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import org.dpr.mykeys.app.profile.ProfileManager;

public class ProfilStoreService implements StoreService<ProfilStoreInfo> {

	public ProfilStoreService(ProfilStoreInfo ksInfo) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<? extends ChildInfo> getChildList() {
		// TODO Auto-generated method stub
		 return ProfileManager.getProfils();
	}

}
