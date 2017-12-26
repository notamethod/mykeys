package org.dpr.mykeys.app.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.certificate.CertificateValue;


public class ProfileManager

{
	public static final Log log = LogFactory.getLog(ProfileManager.class);
	public final static String PROFIL_EXTENSION = ".mkprof";
    private String profilPath;

    public ProfileManager(String profilPath) {
        this.profilPath = profilPath;
    }

	public void saveToFile(Map<String, Object> elements, String name) throws ProfilException, IOException {
		if (StringUtils.isBlank(name)) {
			throw new ProfilException("nom obligatoire");
		}
        File profDir = new File(profilPath);
		if (!profDir.exists()) {
			profDir.mkdirs();
		}
		File f = new File(profDir, name + PROFIL_EXTENSION);
		if (f.exists()) {
			throw new ProfilException("Le profil existe d�j�");
		}
		Properties p = new Properties();
		for (Map.Entry<String, Object> entry : elements.entrySet()) {
			//System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			p.setProperty(entry.getKey(), (String) entry.getValue());
		}
		p.store(new FileOutputStream(f), "");
	}

	public Properties loadProfile(String name) throws ProfilException {
        File f = new File(profilPath, name + PROFIL_EXTENSION);

		if (!f.exists()) {
			throw new ProfilException("Le profil n'existe pas");

		}
		try (FileInputStream fis = new FileInputStream(f)) {
			Properties p = new Properties();
			p.load(new FileInputStream(f));
			return p;
		} catch (Exception e) {
			throw new ProfilException("Erreur chargement profil", e);
		}

	}

    public static List<? extends ChildInfo> getProfils(String cfgPath) {
		List<Profil> profs = new ArrayList<Profil>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(cfgPath))) {
            for (Path path : directoryStream) {
            	profs.add(new Profil(path));

            }
        } catch (IOException ex) {}

		return profs;

	}

	public void saveToFile(Map<String, Object> elements, String name, CertificateValue certInfo)
			throws ProfilException, IOException {
		if (StringUtils.isBlank(name)) {
			throw new ProfilException("nom obligatoire");
		}
        File profDir = new File(profilPath);
		if (!profDir.exists()) {
			profDir.mkdirs();
		}
		File f = new File(profDir, name + PROFIL_EXTENSION);
		if (f.exists()) {
			throw new ProfilException("Le profil existe d�j�");
		}
		Properties p = new Properties();
		for (Map.Entry<String, Object> entry : elements.entrySet()) {
			//System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			p.setProperty(entry.getKey(), (String) entry.getValue());
		}

		p.setProperty("keyUSage", String.valueOf(certInfo.getIntKeyUsage()));
		p.setProperty("keyUSage2", String.valueOf(certInfo.getKeyUsage()));
		p.store(new FileOutputStream(f), "");

	}

	public String[] getProfiles() {
        File profDir = new File(profilPath);
		String[] list = profDir.list((dir, name) -> name.toLowerCase().endsWith(".mkprof"));
		return list;
	}

	public void delete(Profil profil) throws IOException {
		Files.delete(profil.getPath());
		
	}
}
