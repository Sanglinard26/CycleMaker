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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

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

    private static final String ICON_ADD = "/icon_add_24.png";
    private static final String ICON_DEL = "/icon_del_24.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    private final JLabel labelType;
    private final JLabel iconElement;
    private final ElementModel modelElement;
    private final TableElement tableElement;
    private final ComBoDataset comBoDataset;
    private final DefaultComboBoxModel<Dataset> comboBoxModel;
    private final JTextField txtValue, txtDuration, txtAmplitude, txtTpsRampe, txtFrequence, txtNbRepetition;
    private final JTextField txtPosition;

    private Cycle cycle;
    private String selectedForm;

    public PanelCreation() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Design"));

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
        add(new JLabel("Dataset(s)"), gbc);

        comboBoxModel = new DefaultComboBoxModel<>();
        comBoDataset = new ComBoDataset(comboBoxModel);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(comBoDataset, gbc);

        tableElement = new TableElement();
        modelElement = (ElementModel) tableElement.getModel();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
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

        tableElement.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent paramKeyEvent) {
            }

            @Override
            public void keyReleased(KeyEvent paramKeyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent paramKeyEvent) {
                if (paramKeyEvent.getKeyCode() == 127 && tableElement.getSelectedRowCount() > 0) // touche suppr
                {
                    int[] selectedIdx = tableElement.getSelectedRows();

                    if (selectedIdx.length > 0) {

                        for (int nElement = selectedIdx.length - 1; nElement > -1; nElement--) {

                            Element selectedElement = cycle.getDataset(comBoDataset.getSelectedDataset().getName()).getElements()
                                    .get(selectedIdx[nElement]);

                            for (int i = selectedElement.getLastIndex(); i >= selectedElement.getFirstIndex(); i--) {
                                cycle.getDataset(comBoDataset.getSelectedDataset().getName()).getDatas().remove(i);
                            }

                            cycle.removeElementFromDataset(cycle.getDataset(comBoDataset.getSelectedDataset().getName()), selectedElement);

                            modelElement.removeElement(selectedIdx[nElement]);

                        }
                        cycle.updateObservateur("Chart");
                    }
                }
            }
        });

        final JButton btAdd = new JButton(new AbstractAction("", new ImageIcon(getClass().getResource(ICON_ADD))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Element newElement = null;
                        final Dataset grandeur = cycle.getDataset(comBoDataset.getSelectedDataset().getName());

                        int cnt = 1;
                        double nbRepet = getFloatValue(txtNbRepetition.getText());

                        if (selectedForm != null) {
                            do {

                                switch (selectedForm) {
                                case Element.POINT:
                                    newElement = new Point(grandeur, getFloatValue(txtValue.getText()));
                                    break;
                                case Element.CRENEAU:
                                    newElement = new Creneau(grandeur, getFloatValue(txtDuration.getText()), getFloatValue(txtAmplitude.getText()));
                                    break;
                                case Element.STATIONNAIRE:
                                    newElement = new Stationnaire(grandeur, getFloatValue(txtDuration.getText()));
                                    break;
                                case Element.RAMPE:
                                    newElement = new Rampe(grandeur, getFloatValue(txtDuration.getText()), getFloatValue(txtAmplitude.getText()));
                                    break;
                                case Element.SINUS:
                                    newElement = new Sinus(grandeur, getFloatValue(txtAmplitude.getText()), getFloatValue(txtFrequence.getText()));
                                    break;
                                case Element.TRAPEZE:
                                    newElement = new Trapeze(grandeur, getFloatValue(txtDuration.getText()), getFloatValue(txtTpsRampe.getText()),
                                            getFloatValue(txtAmplitude.getText()));
                                    break;
                                default:
                                    break;
                                }

                                if (newElement != null) {

                                    if (modelElement.getRowCount() == 0 && !Element.POINT.equals(selectedForm)) {
                                        JOptionPane.showMessageDialog(PanelCreation.this.getParent(), "The first element must be a <Point>", "Info",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        return;
                                    }

                                    if (txtPosition.getText().isEmpty()) {
                                        cycle.addElementToDataset(grandeur, newElement);
                                        modelElement.addElement(newElement);
                                    } else {
                                        int position = Integer.parseInt(txtPosition.getText());
                                        if (position > 1 && position <= modelElement.getRowCount()) {
                                            cycle.addElementToDataset(grandeur, position, newElement);
                                            modelElement.addElement(Integer.parseInt(txtPosition.getText()) - 1, newElement);
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "The position should be greater than zero and lower than " + modelElement.getRowCount());
                                        }

                                    }

                                }

                                cnt++;
                            } while (cnt <= Math.max(1, nbRepet));

                            cycle.updateObservateur("Chart");
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
        btAdd.setToolTipText("Add an element");
        add(btAdd, gbc);

        final JButton btDel = new JButton(new AbstractAction("", new ImageIcon(getClass().getResource(ICON_DEL))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedIdx = tableElement.getSelectedRows();

                if (selectedIdx.length > 0) {

                    for (int nElement = selectedIdx.length - 1; nElement > -1; nElement--) {

                        Element selectedElement = cycle.getDataset(comBoDataset.getSelectedDataset().getName()).getElements()
                                .get(selectedIdx[nElement]);

                        for (int i = selectedElement.getLastIndex(); i >= selectedElement.getFirstIndex(); i--) {
                            cycle.getDataset(comBoDataset.getSelectedDataset().getName()).getDatas().remove(i);
                        }

                        cycle.removeElementFromDataset(cycle.getDataset(comBoDataset.getSelectedDataset().getName()), selectedElement);

                        modelElement.removeElement(selectedIdx[nElement]);

                    }

                    cycle.updateObservateur("Chart");
                }

            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        btDel.setToolTipText("Remove an element");
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
        add(new JLabel("<html><u>Parameters : </u></html>"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Value of point : "), gbc);

        txtValue = new JTextField(10);
        ((PlainDocument) txtValue.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.DOUBLE_NUMBER));
        txtValue.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
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
        add(new JLabel("Duration : "), gbc);

        txtDuration = new JTextField(10);
        ((PlainDocument) txtDuration.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.DOUBLE_NUMBER));
        txtDuration.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
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
        ((PlainDocument) txtAmplitude.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.DOUBLE_NUMBER));
        txtAmplitude.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
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
        add(new JLabel("Ramp duration : "), gbc);

        txtTpsRampe = new JTextField(10);
        ((PlainDocument) txtTpsRampe.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.DOUBLE_NUMBER));
        txtTpsRampe.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
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
        add(new JLabel("Frequency : "), gbc);

        txtFrequence = new JTextField(10);
        ((PlainDocument) txtFrequence.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.DOUBLE_NUMBER));
        txtFrequence.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
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
        add(new JLabel("Number of repetition : "), gbc);

        txtNbRepetition = new JTextField(10);
        ((PlainDocument) txtNbRepetition.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.INTEGER_NUMBER));
        txtNbRepetition.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
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
        txtPosition.setToolTipText("<html>Not mandatory. <p>If this parameter is empty the element will be add at the end.");
        ((PlainDocument) txtPosition.getDocument()).setDocumentFilter(new NumericDocument(NumericDocument.INTEGER_NUMBER));
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(txtPosition, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
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

        labelType.setText("<html>Type : " + "<b>" + forme);

        txtValue.setText("");
        txtDuration.setText("");
        txtAmplitude.setText("");
        txtTpsRampe.setText("");
        txtFrequence.setText("");
        txtNbRepetition.setText("");

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
        default:
            break;
        }

    }

    private final Float getFloatValue(String txt) {
        if (!txt.isEmpty()) {
            try {
                return Float.parseFloat(txt);
            } catch (NumberFormatException nbf) {
                return 0f;
            }
        }
        return 0f;
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
        comBoDataset.combo.setSelectedIndex(comBoDataset.combo.getItemCount() - 1);
    }

    public final int getIndexDataset() {
        return comBoDataset.getIndexDataset();
    }

    private final class NumericDocument extends DocumentFilter {
        private final String removeRegex;
        public static final String INTEGER_NUMBER = "Integer";
        public static final String DOUBLE_NUMBER = "Double";

        public NumericDocument(String type) {
            if (INTEGER_NUMBER.equals(type)) {
                removeRegex = "\\D";
            } else {
                removeRegex = "[^0-9^\\.^-]";
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {

            text = text.replaceAll(removeRegex, "");

            super.insertString(fb, offset, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

            text = text.replaceAll(removeRegex, "");

            super.replace(fb, offset, length, text, attrs);
        }
    }

    private class ComBoDataset extends JComponent {

        private static final long serialVersionUID = 1L;

        private final JComboBox<Dataset> combo;
        private final JButton btAdd;
        private final JButton btDel;
        private final JButton btRename;

        public ComBoDataset(DefaultComboBoxModel<Dataset> model) {
            super();
            setOpaque(true);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();

            this.combo = new JComboBox<Dataset>(model);
            this.combo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    modelElement.clearList();

                    if (comboBoxModel.getSize() > 0 && !combo.getSelectedItem().toString().isEmpty()) {
                        for (Element element : cycle.getDataset(combo.getSelectedItem().toString()).getElements()) {
                            modelElement.addElement(element);

                        }
                    }

                    for (Component component : PanelCreation.this.getComponents()) {
                        if (component instanceof JTextField && component.isEnabled()) {
                            ((JTextField) component).setText("");
                        }
                    }

                }
            });

            this.btAdd = new JButton(new AbstractAction("<html><b>+</b></html>") {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog("Name of dataset :");
                    if (name != null && !name.isEmpty()) {
                        cycle.addDataset(name);
                        fillDataset();
                    }

                }
            });

            this.btDel = new JButton(new AbstractAction("<html><b>-</b></html>") {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (comboBoxModel.getSize() > 1) {
                        cycle.removeDataset(getSelectedDataset());
                        fillDataset();
                    } else {
                        JOptionPane.showMessageDialog(PanelCreation.this.getParent(),
                                "At least one dataset is required, add one dataset before deleting this one.", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                }
            });

            this.btRename = new JButton(new AbstractAction("a.b.c") {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog("New name of dataset :");
                    if (name != null && !name.trim().isEmpty()) {
                        Dataset selectedDataset = (Dataset) combo.getSelectedItem();
                        selectedDataset.setName(name);
                        fillDataset();
                        cycle.updateObservateur("Dataset");
                    }

                }
            });

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            btAdd.setToolTipText("Add a dataset");
            add(this.btAdd, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            btDel.setToolTipText("Remove this dataset");
            add(this.btDel, gbc);

            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            btRename.setToolTipText("Rename this dataset");
            add(this.btRename, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(this.combo, gbc);
        }

        public final Dataset getSelectedDataset() {
            return (Dataset) this.combo.getSelectedItem();
        }

        public final int getIndexDataset() {
            return this.combo.getSelectedIndex();
        }
    }

}
