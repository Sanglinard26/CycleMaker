package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import form.Cycle;

public final class ListCycleRenderer extends JLabel implements ListCellRenderer<Cycle> {

    private static final long serialVersionUID = 1L;

    private final static String ICON_CYCLE = "/icon_cycle_24.png";

    private final ImageIcon icon = new ImageIcon(getClass().getResource(ICON_CYCLE));

    public ListCycleRenderer() {
    	setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.CENTER);
        setToolTipText("<Suppr> to remove the selected cycle");
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Cycle> list, Cycle value, int index, boolean isSelected, boolean cellHasFocus) {

        setBorder(new EmptyBorder(2, 0, 2, 0));
        setText("<html><b>" + value.getName() + "</b><p>Total time : " + String.format("%.1f", value.getTotalTime()) + "s");
        setIcon(icon);

        if (isSelected) {
            setBackground(UIManager.getDefaults().getColor("List.selectionBackground"));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        return this;
    }

}
