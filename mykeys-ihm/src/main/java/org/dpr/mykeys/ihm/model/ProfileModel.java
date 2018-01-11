package org.dpr.mykeys.ihm.model;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.profile.Profil;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProfileModel extends AbstractTableModel {

    private final String[] entetes = {Messages.getString("label.name"), "Description"};

    private List<Profil> profiles;

    public ProfileModel(List<Profil> profiles) {
        this.profiles = profiles;
    }

    public void setProfiles(List<Profil> profiles) {
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


    public Profil getValueAt(int rowIndex) {
        return profiles.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                // Nom
                return profiles.get(rowIndex).getName();
            default:
                return null;
        }
    }

    public List<Profil> getProfiles() {
        return profiles;
    }
}
