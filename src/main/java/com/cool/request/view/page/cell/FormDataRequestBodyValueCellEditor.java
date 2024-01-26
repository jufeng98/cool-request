package com.cool.request.view.page.cell;

import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class FormDataRequestBodyValueCellEditor extends DefaultCellEditor {
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private final JPanel fileSelectJPanel = new JPanel(new BorderLayout());
    private final JPanel textSelectJPanel = new JPanel(new BorderLayout());
    private final JLabel icon = new JLabel(AllIcons.General.OpenDisk);


    private String name;

    public FormDataRequestBodyValueCellEditor(JTable jTable, Project project) {
        super(new JTextField());

        icon.setSize(50, 50);
        fileSelectJPanel.add(fileJTextField, BorderLayout.CENTER);
        fileSelectJPanel.add(icon, BorderLayout.EAST);
        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                String file = FileChooseUtils.chooseSingleFile(project, null, null);
                if (file == null) return;
                int editingRow = jTable.getEditingRow();
                jTable.setValueAt(file, editingRow, 2);
                fileJTextField.setText(file);
            }
        });
        textSelectJPanel.add(textJTextField, BorderLayout.CENTER);

        ActionListener actionListener = (e) -> {
            if (jTable.isEditing()) {
                jTable.getCellEditor().stopCellEditing();
            }
        };
        textJTextField.addActionListener(actionListener);
        fileJTextField.addActionListener(actionListener);
    }


    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        boolean isText = table.getValueAt(row, 3).equals("text");
        textJTextField.setText(table.getValueAt(row, column).toString());
        fileJTextField.setText(table.getValueAt(row, column).toString());

        textJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        fileJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        name = isText ? "text" : "file";
        if (isText) {
            textJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return textJTextField;
        }
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(fileJTextField, BorderLayout.CENTER);
        jPanel.add(icon, BorderLayout.EAST);
        icon.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return jPanel;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }


    @Override
    public Object getCellEditorValue() {
        if (name.equalsIgnoreCase("file")) return fileJTextField.getText();
        return textJTextField.getText();
    }
}
