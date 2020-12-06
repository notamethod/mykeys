package org.dpr.mykeys.ihm.model;

import org.dpr.mykeys.app.certificate.profile.CertificateTemplate;
import org.dpr.mykeys.ihm.Messages;


import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProfileModel extends AbstractTableModel {

    private final String[] entetes = {Messages.getString("label.name"), "Description"};

    private List<CertificateTemplate> profiles;

    public ProfileModel(List<CertificateTemplate> profiles) {
        this.profiles = profiles;
    }

    public void setProfiles(List<CertificateTemplate> profiles) {
        this.profiles = profiles;
    }

    @Override
    public int getColumnCount() {

        return entetes.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    @Override
    public int getRowCount() {
        return profiles.size();
    }


    public CertificateTemplate getValueAt(int rowIndex) {
        return profiles.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                // Nom
                return profiles.get(rowIndex).getName();
            case 1:
                // Nom
                return profiles.get(rowIndex).getDescription();
            default:
                return null;
        }
    }

    public List<CertificateTemplate> getProfiles() {
        return profiles;
    }
}
