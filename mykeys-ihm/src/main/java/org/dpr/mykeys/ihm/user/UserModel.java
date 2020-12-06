package org.dpr.mykeys.ihm.user;

import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class UserModel extends AbstractTableModel {

    private final String[] entetes = {Messages.getString("label.name"), "Description"};

    private List<Certificate> users;

    public UserModel(List<Certificate> users) {
        this.users = users;
    }

    public void setUsers(List<Certificate> users) {
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


    public Certificate getValueAt(int rowIndex) {
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

    public List<Certificate> getUsers() {
        return users;
    }
}
