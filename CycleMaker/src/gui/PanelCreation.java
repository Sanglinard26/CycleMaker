/*
 * Creation : 25 août 2018
 */
package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import form.Creneau;
import form.Cycle;
import form.Cycle.Dataset;
import form.Element;
import form.Point;
import form.Rampe;
import form.Sinus;
import form.Stationnaire;
import form.Trapeze;

public final class PanelCreation extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final GridBagConstraints gbc = new GridBagConstraints();

    private final JLabel labelType;
    private final JButton btDel, btAdd;
    private final JList<Element> listElement;
    private final DefaultListModel<Element> dataModel;
    private final JComboBox<Dataset> comboBox;
    private final DefaultComboBoxModel<Dataset> comboBoxModel;

    private Cycle cycle;
    private String selectedForm;

    public PanelCreation() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Creation"));
        setPreferredSize(new Dimension(300, 0));

        labelType = new JLabel("Type : ");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(labelType, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("Grandeur(s)"), gbc);

        comboBoxModel = new DefaultComboBoxModel<>();
        comboBox = new JComboBox<>(comboBoxModel);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(comboBox, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("Element(s)"), gbc);

        dataModel = new DefaultListModel<>();
        listElement = new JList<>(dataModel);
        listElement.setFixedCellWidth(80);
        listElement.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JScrollPane(listElement), gbc);

        btAdd = new JButton(new AbstractAction("Ajouter element") {

            @Override
            public void actionPerformed(ActionEvent e) {

                Element newElement = null;
                final Dataset time = cycle.getDataset("Temps");
                final Dataset grandeur = cycle.getDataset("LOOP40");

                switch (selectedForm) {
                case Element.POINT:
                    newElement = new Point(time, grandeur, 2, 5);
                    break;
                case Element.CRENEAU:
                    newElement = new Creneau(time, grandeur, 10, 20);
                    break;
                case Element.STATIONNAIRE:
                    newElement = new Stationnaire(time, grandeur, 10, 20);
                    break;
                case Element.RAMPE:
                    newElement = new Rampe(time, grandeur, 60, 10);
                    break;
                case Element.SINUS:
                    newElement = new Sinus(time, grandeur, 10, 20, 1, 0.5);
                    break;
                case Element.TRAPEZE:
                    newElement = new Trapeze(time, grandeur, 60, 10, 10);
                    break;
                default:
                    break;
                }

                if (!time.getDatas().isEmpty() && newElement != null) {
                    cycle.addElementToDataset(cycle.getDataset("LOOP40"), newElement);
                    dataModel.addElement(cycle.getDataset("LOOP40").getElements().get(cycle.getDataset("LOOP40").getElements().size() - 1));
                } else {
                    newElement = null;
                }
            }
        });
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        add(btAdd, gbc);

        btDel = new JButton(new AbstractAction("Supprimer element") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Element selectedElement = listElement.getSelectedValue();

                if (selectedElement != null) {
                    dataModel.removeElement(selectedElement);

                    for (int i = selectedElement.getLastIndex(); i >= selectedElement.getFirstIndex(); i--) {
                        cycle.getDataset("LOOP40").getDatas().remove(i);
                        cycle.getTime().getDatas().remove(i);
                    }

                    cycle.removeElementFromDataset(cycle.getDataset("LOOP40"), selectedElement);
                }

            }
        });
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        add(btDel, gbc);

        setVisible(false);
    }

    public final void configure(Cycle cycle, String forme) {

        this.cycle = cycle;
        this.selectedForm = forme;

        labelType.setText("Type : " + forme);

        switch (forme) {
        case Element.BASE:

        case Element.CRENEAU:

            break;
        case Element.STATIONNAIRE:

            break;
        case Element.RAMPE:

            break;
        case Element.SINUS:

            break;
        case Element.TRAPEZE:

            break;

        default:
            break;
        }

    }

}
