package org.dpr.mykeys.ihm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.ChildType;

public class Profil implements ChildInfo {

	Properties p;
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Profil(File fic) throws IOException {
		try (FileInputStream fis = new FileInputStream(fic)) {
			 p = new Properties();
			p.load(fis);
			this.name=FilenameUtils.getBaseName(fic.getName());
		} finally {

		}

	}

	@Override
	public ChildType getChildType() {

		return ChildType.PROFILE;
	}

	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return p.getProperty(key);
	}
}
