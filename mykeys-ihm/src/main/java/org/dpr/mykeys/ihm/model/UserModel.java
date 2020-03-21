package org.dpr.mykeys.ihm.model;

import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class UserModel extends AbstractTableModel {

    private final String[] entetes = {Messages.getString("label.name"), "Description"};

    private List<CertificateValue> users;

    public UserModel(List<CertificateValue> users) {
        this.users = users;
    }

    public void setUsers(List<CertificateValue> users) {
        this.users = users;
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
        return users.size();
    }


    public CertificateValue getValueAt(int rowIndex) {
        return users.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                // Nom
                return users.get(rowIndex).getName();
            default:
                return null;
        }
    }

    public List<CertificateValue> getUsers() {
        return users;
    }
}
