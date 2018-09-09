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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
    private final JTextField txtValue, txtDuration, txtAmplitude, txtTpsRampe, txtFrequence, txtNbRepetition;

    private Cycle cycle;
    private String selectedForm;

    public PanelCreation() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Creation"));
        setPreferredSize(new Dimension(400, 0));

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
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(comboBox, gbc);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("Element(s)"), gbc);

        dataModel = new DefaultListModel<>();
        listElement = new JList<>(dataModel);
        listElement.setFixedCellWidth(80);
        listElement.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 7;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        final JScrollPane scrollPane = new JScrollPane(listElement, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, gbc);

        btAdd = new JButton(new AbstractAction("Ajouter element") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {

                Element newElement = null;
                final Dataset time = cycle.getDataset("Temps");
                final Dataset grandeur = cycle.getDataset(comboBox.getSelectedItem().toString());

                int cnt = 1;

                do {

                    switch (selectedForm) {
                    case Element.POINT:
                        newElement = new Point(time, grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtValue.getText()));
                        break;
                    case Element.CRENEAU:
                        newElement = new Creneau(time, grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtAmplitude.getText()));
                        break;
                    case Element.STATIONNAIRE:
                        newElement = new Stationnaire(time, grandeur, getDoubleValue(txtDuration.getText()));
                        break;
                    case Element.RAMPE:
                        newElement = new Rampe(time, grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtAmplitude.getText()));
                        break;
                    case Element.SINUS:
                        newElement = new Sinus(time, grandeur, getDoubleValue(txtAmplitude.getText()), getDoubleValue(txtFrequence.getText()));
                        break;
                    case Element.TRAPEZE:
                        newElement = new Trapeze(time, grandeur, getDoubleValue(txtDuration.getText()), getDoubleValue(txtTpsRampe.getText()),
                                getDoubleValue(txtAmplitude.getText()));
                        break;
                    }

                    if (!time.getDatas().isEmpty() && newElement != null) {
                        cycle.addElementToDataset(grandeur, newElement);
                        dataModel.addElement(grandeur.getElements().get(grandeur.getElements().size() - 1));
                    } else {
                        newElement = null;
                    }

                    cnt++;
                } while (cnt <= Math.max(1, getDoubleValue(txtNbRepetition.getText())));

            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        add(btAdd, gbc);

        btDel = new JButton(new AbstractAction("Supprimer element") {

            private static final long serialVersionUID = 1L;

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
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        add(btDel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("<html><u>Parametres : </u></html>"), gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
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
        gbc.insets = new Insets(0, 0, 0, 0);
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
        gbc.insets = new Insets(0, 0, 0, 0);
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
        gbc.insets = new Insets(0, 0, 0, 0);
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
        gbc.insets = new Insets(0, 0, 0, 0);
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
        gbc.insets = new Insets(0, 0, 0, 0);
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
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        ImageIcon icon = new ImageIcon(getClass().getResource("/icon_point_128.png"));
        add(new JLabel(icon), gbc);

        setVisible(false);
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

        switch (forme) {
        case Element.POINT:
            txtValue.setEnabled(true);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(false);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            break;
        case Element.CRENEAU:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            break;
        case Element.STATIONNAIRE:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(false);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            break;
        case Element.RAMPE:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            break;
        case Element.SINUS:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(false);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(false);
            txtFrequence.setEnabled(true);
            txtNbRepetition.setEnabled(true);
            break;
        case Element.TRAPEZE:
            txtValue.setEnabled(false);
            txtDuration.setEnabled(true);
            txtAmplitude.setEnabled(true);
            txtTpsRampe.setEnabled(true);
            txtFrequence.setEnabled(false);
            txtNbRepetition.setEnabled(true);
            break;
        }

    }

    private final double getDoubleValue(String txt) {
        if (!txt.isEmpty()) {
            return Double.parseDouble(txt);
        }
        return 0d;
    }
    
    public final void setCycle(Cycle cycle)
    {
    	this.cycle = cycle;
    }
    
    public final void fillDataset(Cycle cycle)
    {
    	for(Dataset dataset : cycle.getDatasets())
    	{
    		if(comboBoxModel.getIndexOf(dataset)<0 && !"Temps".equals(dataset.getName()))
    		{
    			comboBoxModel.addElement(dataset);
    		}
    	}
    }

}
