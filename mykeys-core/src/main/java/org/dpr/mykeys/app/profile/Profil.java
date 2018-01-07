package org.dpr.mykeys.app.profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.ChildType;

public class Profil implements ChildInfo {

	private Properties p;
	private String name;
	private Path path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	public Profil(Path path) throws IOException {
		try (InputStream fis = Files.newInputStream(path)){
			p = new Properties();
			p.load(fis);
		
			this.name = FilenameUtils.getBaseName(path.getFileName().toString());
			this.path=path;
		} finally {

		}

	}

	@Override
	public ChildType getChildType() {

		return ChildType.PROFILE;
	}

	public String getValue(String key) {
		// TODO Auto-generated method stub
		return p.getProperty(key);
	}

	public int getIntValue(String string) {
		if (string!=null){
			try {
				return Integer.valueOf(getValue(string));
			} catch (NumberFormatException e) {
				//not a number
				
			}
		}
		return 0;
		
	}

	public Path getPath() {
		// TODO Auto-generated method stub
		return path;
	}
}
