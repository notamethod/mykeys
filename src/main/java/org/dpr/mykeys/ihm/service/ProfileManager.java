package org.dpr.mykeys.ihm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.ihm.windows.ManageProfilException;

public class ProfileManager
{
    public void saveToFile(Map<String, Object> elements, String name) throws ManageProfilException, IOException
    { 
        if (StringUtils.isBlank(name))
        {
            throw new ManageProfilException("nom obligatoire");
        }
        File f = new File(KSConfig.getCfgPath(), name + ".mkprof");
        if (f.exists())
        {
            throw new ManageProfilException("Le profil existe d�j�");
        }
        Properties p = new Properties();
        for (Map.Entry<String, Object> entry : elements.entrySet())
        {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
            p.setProperty(entry.getKey(), (String) entry.getValue());
        }
        p.store(new FileOutputStream(f), "");
    }

    public Properties loadProfile(String name) throws ManageProfilException
    {
        File f = new File(KSConfig.getCfgPath(), name + ".mkprof");

        if (!f.exists())
        {
            throw new ManageProfilException("Le profil n'existe pas");

        }
        try (FileInputStream fis = new FileInputStream(f))
        {
            Properties p = new Properties();
            p.load(new FileInputStream(f));
            return p;
        }
        catch (Exception e)
        {
            throw new ManageProfilException("Erreur chargement profil", e);
        }


    }
}
