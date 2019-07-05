/*
 * Copyright 2018 org.dpr & croger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dpr.mykeys.ihm.model;


import org.dpr.mykeys.utils.X509Util;
import org.dpr.mykeys.app.crl.CRLEntry;
import org.dpr.mykeys.app.crl.CRLManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CRLEntryModel extends AbstractTableModel {

    private final List<CRLEntry> certificates = new ArrayList<>();

    public CRLEntryModel() {
        super();

    }

    private final String[] colName = new String[]{"Serial", "Subject", "Date", "Reason"};

    public void setCertificates(List<CRLEntry> albums) {
        this.certificates.clear();
        this.certificates.addAll(albums);
        fireTableDataChanged();
    }

    public void addAlbum(CRLEntry entry) {

        this.certificates.add(entry);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int i) {
        return colName[i];
    }

    @Override
    public int getRowCount() {
        if (certificates == null)
            return 0;
        return certificates.size();
    }

    @Override
    public int getColumnCount() {
        return colName.length;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 99:
                return ImageIcon.class;
            default:
                return super.getColumnClass(column);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        CRLEntry entry = null;
        entry = certificates.get(row);

        switch (column) {

            case 0:
                return X509Util.toHexString(entry.getSerialNumber(), " ", true);
            case 1:
                return entry.getSubject();
            case 2:
                return entry.getRevocationDate();
            case 3:
                return CRLManager.REASONSTRING[entry.getReason()];

            default:

                return "";
        }

    }

    public CRLEntry getRow(int row) {
        return certificates.get(row);
    }

    public void clear() {
        this.certificates.clear();
    }

    public void addRow(CRLEntry data) {
        certificates.add(data);
        fireTableRowsInserted(certificates.size() - 1, certificates.size() - 1);
    }

    public List<CRLEntry> getValues() {
        return certificates;
    }
}
