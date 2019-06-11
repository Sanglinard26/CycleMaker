package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import form.Cycle;

public final class ListCycleRenderer extends JComponent implements ListCellRenderer<Cycle> {

    private static final long serialVersionUID = 1L;

    private final static String ICON_CYCLE = "/icon_cycle_24.png";

    private static final JLabel cycleName = new JLabel();

    private final ImageIcon icon = new ImageIcon(getClass().getResource(ICON_CYCLE));

    static {
        cycleName.setOpaque(true);
        cycleName.setHorizontalAlignment(SwingConstants.LEFT);
        cycleName.setVerticalAlignment(SwingConstants.CENTER);
    }

    public ListCycleRenderer() {

        setLayout(new BorderLayout());

        add(cycleName, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Cycle> list, Cycle value, int index, boolean isSelected, boolean cellHasFocus) {

        setBorder(new EmptyBorder(2, 0, 2, 0));
        cycleName.setText("<html><b>" + value.getName() + "</b><p>Total time : " + String.format("%.1f", value.getTotalTime()) + "s");
        cycleName.setIcon(icon);

        if (isSelected) {
            cycleName.setBackground(UIManager.getDefaults().getColor("Menu.background"));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else {
            cycleName.setBackground(Color.WHITE);
        }

        return this;
    }

}
