/*
 * Creation : 17 août 2018
 */
package form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import observer.Observable;
import observer.Observateur;
import utils.Utilitaire;

public final class Cycle implements Observable, Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Dataset> datasets;
    private Time baseTime;

    private transient List<Observateur> listObservateur = new ArrayList<Observateur>();

    public Cycle(File file) {

        if (Utilitaire.getExtension(file).equals("cycle")) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Cycle cycle = (Cycle) ois.readObject();
                this.name = cycle.getName();
                this.datasets = cycle.getDatasets();
                this.baseTime = cycle.getTime();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            parse(file);
        }

    }

    public Cycle(String name, List<String> datasets) {
        this.name = name;
        this.datasets = new ArrayList<Dataset>();
        this.baseTime = new Time();

        for (String dataset : datasets) {
            this.addDataset(dataset.trim());
        }
    }

    private final void parse(File file) {

        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {

            this.name = Utilitaire.getFileNameWithoutExtension(file);
            this.baseTime = new Time();

            String line;
            String[] splitLine;
            int cntLine = 0;

            while ((line = bf.readLine()) != null) {

                splitLine = line.split("\t");

                if (cntLine > 0) {

                    for (int idxCol = 0; idxCol < splitLine.length; idxCol++) {
                        if (idxCol == 0) {
                            this.baseTime.add(Double.parseDouble(splitLine[idxCol].trim()));
                        } else {
                            this.datasets.get(idxCol - 1).addData(Double.parseDouble(splitLine[idxCol].trim()));
                        }

                    }

                } else {
                    datasets = new ArrayList<Dataset>(splitLine.length - 1);

                    for (String nameDataset : splitLine) {
                        if (!"Temps".equals(nameDataset)) {
                            this.datasets.add(new Dataset(nameDataset));
                        }
                    }
                }

                cntLine++;
            }

            for (Dataset dataset : this.datasets) {
                addElementToDataset(dataset, new Base(this.baseTime, dataset));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final boolean save(File file) {

        try (PrintWriter writer = new PrintWriter(file)) {

            long start = System.currentTimeMillis();

            final int nbDataset = getNbDataset();
            int nbPoint = Integer.MAX_VALUE;

            final DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Formatage des valeurs avec deux decimales
            final DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();

            dfs.setDecimalSeparator('.');
            decimalFormat.setDecimalFormatSymbols(dfs);

            writer.print("Temps" + "\t");

            for (int numDataset = 0; numDataset < nbDataset; numDataset++) {
                if (numDataset != nbDataset - 1) {
                    writer.print(this.datasets.get(numDataset).getName() + "\t");
                } else {
                    writer.println(this.datasets.get(numDataset).getName());
                }

                nbPoint = Math.min(nbPoint, this.datasets.get(numDataset).getNbPoint());
            }

            for (int numPoint = 0; numPoint < nbPoint; numPoint++) {
                writer.print(decimalFormat.format(this.baseTime.get(numPoint)) + "\t");
                for (int numDataset = 0; numDataset < nbDataset; numDataset++) {
                    if (numDataset != nbDataset - 1) {
                        writer.print(decimalFormat.format(this.datasets.get(numDataset).getDatas().get(numPoint)) + "\t");
                    } else {
                        if (numPoint != this.baseTime.size() - 1) {
                            writer.println(decimalFormat.format(this.datasets.get(numDataset).getDatas().get(numPoint)));
                        } else {
                            writer.print(decimalFormat.format(this.datasets.get(numDataset).getDatas().get(numPoint)));
                        }

                    }
                }
            }

            serialize(file);

            System.out.println(System.currentTimeMillis() - start);

            return true;

        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public final void serialize(File file) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath().replace(".txt", ".cycle")))) {
            oos.writeObject(this);
            oos.flush();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public final int getNbPoint() {
        this.baseTime.grow();
        return this.baseTime.size();
    }

    public final void addDataset(String name) {
        if (!this.datasets.contains(new Dataset(name))) {
            this.datasets.add(new Dataset(name));
            updateObservateur("Dataset");
        }
    }

    public final void removeDataset(Dataset dataset) {
        this.datasets.remove(dataset);
        updateObservateur("Dataset");
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public final int getNbDataset() {
        return datasets.size();
    }

    public final Dataset getDataset(String name) {
        for (Dataset dataset : this.datasets) {
            if (dataset.equals(name)) {
                return dataset;
            }
        }
        return null;
    }

    public final double getTotalTime() {

        this.baseTime.grow();
        int nbPoint = Integer.MAX_VALUE;
        for (Dataset dataset : this.getDatasets()) {
            nbPoint = Math.min(nbPoint, dataset.getNbPoint());
        }
        if (nbPoint > 0) {
            return this.baseTime.get(nbPoint - 1);
        }
        return 0;
    }

    public final Time getTime() {
        if (this.baseTime != null) {
            return this.baseTime;
        }
        return new Time();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final void addElementToDataset(Dataset dataset, Element form) {

        dataset.addElement(form);
        this.baseTime.grow();

        form.setT1(this.baseTime.get(form.getFirstIndex()));
        form.setT2(this.baseTime.get(form.getLastIndex()));

        updateObservateur("Chart");
    }

    public final void addElementToDataset(Dataset dataset, int position, Element form) {

        final Element previousElement = dataset.getElements().get(position - 2); // -2 car c'est une position en base 1 qui entre dans la méthode
        int lastIdxPrev = previousElement.getLastIndex();

        final double removeAmplitude = dataset.getDatas().get(previousElement.getLastIndex()) - dataset.getDatas().get(form.getFirstIndex());

        dataset.addElement(position, form);

        this.baseTime.grow();

        List<Double> moveDatas = new ArrayList<Double>();

        for (int nData = dataset.getElements().get(position - 1).getLastIndex(); nData >= dataset.getElements().get(position - 1)
                .getFirstIndex(); nData--) {
            moveDatas.add(dataset.getDatas().remove(nData));
        }

        int cnt = previousElement.getLastIndex();

        for (int nData = moveDatas.size() - 1; nData >= 0; nData--) {
            dataset.getDatas().add(++cnt, moveDatas.get(nData));
        }

        for (int nElement = position - 1; nElement < dataset.getElements().size(); nElement++) {
            Element thisElement = dataset.getElements().get(nElement);

            thisElement.setFirstIndex(++lastIdxPrev);
            lastIdxPrev += (thisElement.getNbPoint() - 1);
            thisElement.setLastIndex(lastIdxPrev);

            thisElement.setT1(this.baseTime.get(thisElement.getFirstIndex()));
            thisElement.setT2(this.baseTime.get(thisElement.getLastIndex()));

        }

        for (int i = form.firstIndex; i <= form.lastIndex; i++) {
            double oldValue = dataset.datas.get(i);
            dataset.datas.set(i, oldValue + removeAmplitude);
        }

        double lastValueForm = dataset.datas.get(form.lastIndex);
        double diff = lastValueForm - dataset.datas.get(form.lastIndex + 1);

        for (int i = form.lastIndex + 1; i <= dataset.datas.size() - 1; i++) {
            double oldValue = dataset.datas.get(i);
            dataset.datas.set(i, oldValue + diff);
        }

        updateObservateur("Chart");
    }

    public final void removeElementFromDataset(Dataset dataset, Element form) {

        final int idx1 = form.getFirstIndex();
        final int idx2 = form.getLastIndex();
        final double removeAmplitude = form.diffEndFromBeginValue();
        final int removeNbPoint = form.getNbPoint();

        dataset.removeElement(form);

        for (int i = idx1; i < dataset.getDatas().size(); i++) {
            double value = dataset.getDatas().get(i);
            dataset.getDatas().set(i, value - removeAmplitude);
        }

        for (Element element : dataset.getElements()) {

            int firstIndex = element.getFirstIndex();
            int lastIndex = element.getLastIndex();

            if (firstIndex > idx2) {
                element.setFirstIndex(Math.max(0, firstIndex - removeNbPoint));
                element.setLastIndex(Math.max(0, lastIndex - removeNbPoint));

                element.setT1(this.baseTime.get(element.getFirstIndex()));
                element.setT2(this.baseTime.get(element.getLastIndex()));
            }

        }

        updateObservateur("Chart");
    }

    public final class Dataset implements Serializable {

        private static final long serialVersionUID = 1L;

        private String name;
        private List<Double> datas;
        private List<Element> elements;

        public Dataset(String name) {
            this.name = name;
            this.datas = new ArrayList<Double>();
            this.elements = new ArrayList<Element>();
        }

        public final void addData(Double data) {
            this.datas.add(data);
        }

        private final void addElement(Element element) {
            this.elements.add(element);
            element.setPosition(this.elements.size());
        }

        private final void addElement(int position, Element element) {
            this.elements.add(position - 1, element);
            element.setPosition(position);
            for (int pos = 0; pos < this.elements.size(); pos++) {
                this.elements.get(pos).setPosition(pos + 1);
            }
        }

        private final void removeElement(Element element) {
            this.elements.remove(element);
            for (int pos = 0; pos < this.elements.size(); pos++) {
                this.elements.get(pos).setPosition(pos + 1);
            }
        }

        public final void setName(String newName) {
            this.name = newName;
        }

        public final String getName() {
            return name;
        }

        public final List<Double> getDatas() {
            return datas;
        }

        public final int getNbPoint() {
            return datas.size();
        }

        public final List<Element> getElements() {
            return elements;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null ? this.name.equals(obj.toString()) : false;
        }
    }

    public final class Time extends ArrayList<Double> {

        private static final long serialVersionUID = 1L;

        public Time() {
            super();
        }

        public final void grow() {

            int nbPoint = 0;
            int diffPoint = 0;

            if (isEmpty()) {
                add(0d);
            }

            for (Dataset dataset : datasets) {
                nbPoint = Math.max(nbPoint, dataset.getNbPoint());
            }

            diffPoint = nbPoint - size();

            while (diffPoint-- > 0) {
                add(get(size() - 1) + Element.te);
            }

        }
    }

    @Override
    public void addObservateur(Observateur obs) {
        this.listObservateur.add(obs);

    }

    @Override
    public void updateObservateur(String property) {
        for (Observateur obs : this.listObservateur) {
            obs.update(property);
        }
    }

    @Override
    public void delObservateur() {
        this.listObservateur = new ArrayList<Observateur>();

    }

}
