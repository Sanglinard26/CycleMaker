/*
 * Creation : 25 août 2018
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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

    final String ICON_ADD = "/icon_add_24.png";
    final String ICON_DEL = "/icon_del_24.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    private final JLabel labelType, iconElement;
    private final JButton btDel, btAdd, btAddDataset;
    private final ElementModel modelElement;
    private final TableElement tableElement;
    private final JComboBox<Dataset> comboBox;
    private final DefaultComboBoxModel<Dataset> comboBoxModel;
    private final JTextField txtValue, txtDuration, txtAmplitude, txtTpsRampe, txtFrequence, txtNbRepetition;
    private final JTextField txtPosition;

    private Cycle cycle;
    private String selectedForm;

    public PanelCreation() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Creation"));

        labelType = new JLabel("Type : ");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(labelType, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("Grandeur(s)"), gbc);

        comboBoxModel = new DefaultComboBoxModel<>();
        comboBox = new JComboBox<>(comboBoxModel);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.EAST;
        comboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modelElement.clearList();

                if (comboBoxModel.getSize() > 0 && !comboBox.getSelectedItem().toString().isEmpty()) {
                    for (Element element : cycle.getDataset(comboBox.getSelectedItem().toString()).getElements()) {
                        // modelElement.addRow(new Object[] { element.getPosition(), element });
                        modelElement.addElement(element);

                    }
                }

                for (Component component : PanelCreation.this.getComponents()) {
                    if (component instanceof JTextField && component.isEnabled()) {
                        ((JTextField) component).setText(null);
                    }
                }

            }
        });
        add(comboBox, gbc);

        btAddDataset = new JButton(new AbstractAction("<html><b>+</b></html>") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Nom de la grandeur :");
                if (name != null && !name.isEmpty()) {
                    cycle.addDataset(name);
                    fillDataset();
                }

            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(btAddDataset, gbc);

        tableElement = new TableElement();
        modelElement = (ElementModel) tableElement.getModel();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        final JScrollPane scrollPane = new JScrollPane(tableElement, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, gbc);
        tableElement.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (modelElement.getRowCount() > 0 && !e.getValueIsAdjusting() && tableElement.getSelectedRowCount() > 0) {

                    PanelCreation.this.firePropertyChange("selectedElement", null, modelElement.getValueAt(tableElement.getSelectedRow(), 1));
                }

            }
        });

        btAdd = new JButton(new AbstractAction("", new ImageIcon(getClass().getResource(ICON_ADD))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Element newElement = null;
                        final Dataset grandeur = cycle.getDataset(comboBox.getSelectedItem().toString());

                        int cnt = 1;
                        double nbRepet = getDoubleValue(txtNbRepetition.getText());

                        if (selectedForm != null) {
                            do {

                                switch (selectedForm) {
                                case Element.POINT:
                                    newElement = new Point(grandeur, getDoubleValue(txtValue.getText()));
                                    break;
                                case Element.CRENEAU:
                                    newElement = new Creneau(grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtAmplitude.getText()));
                                    break;
                                case Element.STATIONNAIRE:
                                    newElement = new Stationnaire(grandeur, getDoubleValue(txtDuration.getText()));
                                    break;
                                case Element.RAMPE:
                                    newElement = new Rampe(grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtAmplitude.getText()));
                                    break;
                                case Element.SINUS:
                                    newElement = new Sinus(grandeur, getDoubleValue(txtAmplitude.getText()), getDoubleValue(txtFrequence.getText()));
                                    break;
                                case Element.TRAPEZE:
                                    newElement = new Trapeze(grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtTpsRampe.getText()),
                                            getDoubleValue(txtAmplitude.getText()));
                                    break;
                                }

                                if (newElement != null) {

                                    if (txtPosition.getText().isEmpty()) {
                                        cycle.addElementToDataset(grandeur, newElement);
                                        modelElement.addElement(newElement);
                                    } else {
                                        cycle.addElementToDataset(grandeur, Integer.parseInt(txtPosition.getText()), newElement);
                                        modelElement.addElement(Integer.parseInt(txtPosition.getText()) - 1, newElement);
                                    }

                                }

                                cnt++;
                            } while (cnt <= Math.max(1, nbRepet));
                        }

                    }
                });

                thread.start();

            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btAdd, gbc);

        btDel = new JButton(new AbstractAction("", new ImageIcon(getClass().getResource(ICON_DEL))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedIdx = tableElement.getSelectedRows();

                if (selectedIdx.length > 0) {

                    for (int nElement = selectedIdx.length - 1; nElement > -1; nElement--) {

                        Element selectedElement = cycle.getDataset(comboBox.getSelectedItem().toString()).getElements().get(selectedIdx[nElement]);

                        for (int i = selectedElement.getLastIndex(); i >= selectedElement.getFirstIndex(); i--) {
                            cycle.getDataset(comboBox.getSelectedItem().toString()).getDatas().remove(i);
                        }

                        cycle.removeElementFromDataset(cycle.getDataset(comboBox.getSelectedItem().toString()), selectedElement);

                        // modelElement.removeRow(selectedIdx[nElement]);
                        modelElement.removeElement(selectedIdx[nElement]);

                    }

                    // for (int i = 0; i < cycle.getDataset(comboBox.getSelectedItem().toString()).getElements().size(); i++) {
                    // modelElement.setValueAt(cycle.getDataset(comboBox.getSelectedItem().toString()).getElements().get(i).getPosition(), i, 0);
                    // }

                }

            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btDel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("<html><u>Parametres : </u></html>"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Valeur du point : "), gbc);

        txtValue = new JTextField(10);
        txtValue.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(txtValue, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Duree : "), gbc);

        txtDuration = new JTextField(10);
        txtDuration.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(txtDuration, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Amplitude : "), gbc);

        txtAmplitude = new JTextField(10);
        txtAmplitude.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(txtAmplitude, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Duree de la rampe : "), gbc);

        txtTpsRampe = new JTextField(10);
        txtTpsRampe.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(txtTpsRampe, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Frequence : "), gbc);

        txtFrequence = new JTextField(10);
        txtFrequence.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(txtFrequence, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Nombre de repetition : "), gbc);

        txtNbRepetition = new JTextField(10);
        txtNbRepetition.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(txtNbRepetition, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Position : "), gbc);

        txtPosition = new JTextField(5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(txtPosition, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 8;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        iconElement = new JLabel();
        iconElement.setMinimumSize(new Dimension(200, 200));
        iconElement.setPreferredSize(new Dimension(200, 200));
        iconElement.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(iconElement, gbc);

        setVisible(true);
    }

    public final void configure(Cycle cycle, String forme) {

        this.cycle = cycle;
        this.selectedForm = forme;

        labelType.setText("Type : " + forme);

        txtValue.setText(null);
        txtDuration.setText(null);
        txtAmplitude.setText(null);
        txtTpsRampe.setText(null);
        txtFrequence.setText(null);
        txtNbRepetition.setText(null);

        iconElement.setIcon(null);

        switch (forme) {
        case Element.POINT:
            txtValue.setEnabled(true);
            txtDuration.setEnabled(false);
            txtAmplitude.setEnabled(false);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(false);
            iconElement.setIcon(new ImageIcon(getClass().getResource(Element.ICON_POINT)));
            break;
        case Element.CRENEAU:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            iconElement.setIcon(new ImageIcon(getClass().getResource(Element.ICON_CRENEAU)));
            break;
        case Element.STATIONNAIRE:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(false);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            iconElement.setIcon(new ImageIcon(getClass().getResource(Element.ICON_STATIONNAIRE)));
            break;
        case Element.RAMPE:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            iconElement.setIcon(new ImageIcon(getClass().getResource(Element.ICON_RAMPE)));
            break;
        case Element.SINUS:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(false);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(true);
            txtNbRepetition.setEnabled(true);
            iconElement.setIcon(new ImageIcon(getClass().getResource(Element.ICON_SINUS)));
            break;
        case Element.TRAPEZE:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(true);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            iconElement.setIcon(new ImageIcon(getClass().getResource(Element.ICON_TRAPEZE)));
            break;
        }

    }

    private final double getDoubleValue(String txt) {
        if (!txt.isEmpty()) {
            return Double.parseDouble(txt);
        }
        return 0d;
    }

    public final void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    public final void fillDataset() {
        comboBoxModel.removeAllElements();
        for (Dataset dataset : cycle.getDatasets()) {
            if (comboBoxModel.getIndexOf(dataset) < 0 && !"Temps".equals(dataset.getName())) {
                comboBoxModel.addElement(dataset);
            }
        }
    }

    public final int getIndexDataset() {
        return comboBox.getSelectedIndex();
    }

}
