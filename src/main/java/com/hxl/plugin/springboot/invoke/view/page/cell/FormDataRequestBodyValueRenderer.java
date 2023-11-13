package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormDataRequestBodyValueRenderer extends JPanel implements TableCellRenderer {
    private final JPanel fileSelectJPanel = new JPanel(new BorderLayout());
    private final JPanel textSelectJPanel = new JPanel(new BorderLayout());
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private final JLabel fileSelectJLabel =new JLabel(AllIcons.General.OpenDisk);
    private final CardLayout cardLayout =new CardLayout();


    public FormDataRequestBodyValueRenderer() {
        this.setLayout(cardLayout);
        setOpaque(true);
        fileSelectJPanel.add(fileJTextField,BorderLayout.CENTER);
        fileSelectJPanel.add(fileSelectJLabel,BorderLayout.EAST);

        textSelectJPanel.add(textJTextField,BorderLayout.CENTER);

        this.add("file",fileSelectJPanel);
        this.add("text",textSelectJPanel);

    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
        System.out.println("getTableCellRendererComponent  "+value +"  "+table.getValueAt(row,column).toString());

        if (table.getValueAt(row, 2).equals("text")){
            cardLayout.show(this,"text");
        }else{
            cardLayout.show(this,"file");
        }
        textJTextField.setText(table.getValueAt(row,column).toString());
        fileJTextField.setText(table.getValueAt(row,column).toString());
        return this;
    }
}