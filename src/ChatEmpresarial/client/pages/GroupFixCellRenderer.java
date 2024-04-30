/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;

import ChatEmpresarial.shared.models.Grupo;
import java.awt.Component;
import javax.swing.*;
import javax.swing.DefaultListCellRenderer;

/**
 *
 * @author Galle
 */
public class GroupFixCellRenderer extends DefaultListCellRenderer
{
    
    /**
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    
    
    @Override
    public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        int ID = ((Grupo)value).getId_grupo();
        
        setText(Integer.toString(ID));
        return this;
    }
    
    
}
