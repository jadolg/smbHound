/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smbhound;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author akiel
 */
public class HTableCellRenderer extends JLabel implements TableCellRenderer {
        @Override

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
            setBackground(Color.getHSBColor(86,119, 252));
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/smbhound/ic_content_copy_black_18dp.png")));
//        setText((value == null) ? "" : value.toString());
        return this;
    }
}
