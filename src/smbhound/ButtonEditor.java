/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smbhound;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author akiel
 */
class ButtonEditor extends DefaultCellEditor {

    protected JLabel button;
    private String label;
    private boolean isPushed;
    private String copiar = "";

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JLabel();
        button.setOpaque(true);
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/smbhound/ic_content_copy_black_18dp.png")));
        button.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
                
            }

            @Override
            public void mousePressed(MouseEvent me) {
                fireEditingStopped();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                
            }

            @Override
            public void mouseExited(MouseEvent me) {
                
            }
        }
        );
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                fireEditingStopped();
//            }
//
//        });
    }

    @Override

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        button.setBackground(Color.getHSBColor(86,119, 252));
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/smbhound/ic_content_copy_black_18dp.png")));
        copiar = (String) table.getModel().getValueAt(row, 0);
//        label = (value == null) ? "" : value.toString();
//        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {

            //            JOptionPane.showMessageDialog(button, label + ": Ouch!");
//            JOptionPane.showMessageDialog(button, copiar);
            StringSelection selection = new StringSelection(copiar);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
