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

package org.dpr.mykeys.ihm.components;


import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.crl.CRLEntry;
import org.dpr.mykeys.app.utils.X509Util;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CryptoElementModel extends AbstractTableModel {

    private final List<Certificate> certificates = new ArrayList<>();

    public CryptoElementModel() {
        super();
    }

    private final String[] colName = new String[]{"Type", "Info", "a", "b", "c"};

    public void setCertificates(List<Certificate> certificates) {
        this.certificates.clear();
        this.certificates.addAll(certificates);
        fireTableDataChanged();
    }

    public void addElement(Certificate entry) {

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
        Certificate entry = null;
        entry = certificates.get(row);
        switch (column) {

            case 0:
                return "X509";
            case 1:
                return entry.getSubjectString();//+X509Util.toHexString(entry.getSerialNumber(), " ", true)
            default:
                return "";
        }

    }

    public Certificate getRow(int row) {
        return certificates.get(row);
    }

    public List<Certificate> getRows(int[] rows) {
        List<Certificate> filtered = IntStream.of(rows)
                .boxed()
                .map(certificates::get)
                .collect(Collectors.toList());
        return filtered;
    }

    public void clear() {
        this.certificates.clear();
    }

    public void addRow(Certificate data) {
        certificates.add(data);
        fireTableRowsInserted(certificates.size() - 1, certificates.size() - 1);
    }

    public List<Certificate> getValues() {
        return certificates;
    }
}
