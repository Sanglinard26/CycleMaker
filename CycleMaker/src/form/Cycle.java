/*
 * Creation : 17 août 2018
 */
package form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import observer.Observable;
import observer.Observateur;
import utils.Utilitaire;

public final class Cycle implements Observable {

    private String name;
    private List<Dataset> datasets;
    private Dataset time;

    private List<Observateur> listObservateur = new ArrayList<Observateur>();

    public Cycle(File file) {
        parse(file);
    }

    public Cycle(String name, String grandeurs) {
        this.name = name;
        this.datasets = new ArrayList<Dataset>();
        this.addDataset("Temps");
        this.time = this.getDataset("Temps");

        for (String grandeur : grandeurs.split(",")) {
            this.addDataset(grandeur.trim());
        }
    }

    private final void parse(File file) {

        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {

            this.name = Utilitaire.getFileNameWithoutExtension(file);

            String line;
            String[] splitLine;
            int cntLine = 0;

            while ((line = bf.readLine()) != null) {

                splitLine = line.split("\t");

                if (cntLine > 0) {

                    for (int idxCol = 0; idxCol < splitLine.length; idxCol++) {
                        this.datasets.get(idxCol).addData(Double.parseDouble(splitLine[idxCol].trim()));
                    }

                } else {
                    datasets = new ArrayList<Dataset>(splitLine.length);

                    for (String nameDataset : splitLine) {
                        this.datasets.add(new Dataset(nameDataset));
                    }
                }

                cntLine++;
            }

            for (Dataset dataset : this.datasets) {
                if ("Temps".equals(dataset.getName())) {
                    time = dataset;
                } else {
                    dataset.addElement(new Base(time, dataset, 0, 0));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final boolean save(File file) {

        try (PrintWriter writer = new PrintWriter(file)) {

            for (int numDataset = 0; numDataset < this.datasets.size(); numDataset++) {
                if (numDataset != this.datasets.size() - 1) {
                    writer.print(this.datasets.get(numDataset).getName() + "\t");
                } else {
                    writer.println(this.datasets.get(numDataset).getName());
                }
            }

            for (int numPoint = 0; numPoint < this.time.getDatas().size(); numPoint++) {
                for (int numDataset = 0; numDataset < this.datasets.size(); numDataset++) {
                    if (numDataset != this.datasets.size() - 1) {
                        writer.print(this.datasets.get(numDataset).getDatas().get(numPoint) + "\t");
                    } else {
                        if (numPoint != this.time.getDatas().size() - 1) {
                            writer.println(this.datasets.get(numDataset).getDatas().get(numPoint));
                        } else {
                            writer.print(this.datasets.get(numDataset).getDatas().get(numPoint));
                        }

                    }
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public final void addDataset(String name) {
        if (!this.datasets.contains(new Dataset(name))) {
            this.datasets.add(new Dataset(name));
            updateObservateur("Dataset");
        }
    }

    public final void removeDataset(Dataset dataset) {
        this.datasets.remove(dataset);
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public final Dataset getDataset(String name) {
        for (Dataset dataset : this.datasets) {
            if (dataset.equals(name)) {
                return dataset;
            }
        }
        return null;
    }

    public final Dataset getTime() {
        if (this.time != null) {
            return this.time;
        }
        return new Dataset("Temps");
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final void addElementToDataset(Dataset dataset, Element form) {
        dataset.addElement(form);
        updateObservateur("Chart");
    }

    public final void removeElementFromDataset(Dataset dataset, Element form) {

        final int idx1 = form.getFirstIndex();
        final int idx2 = form.getLastIndex();
        final double removeDuration = form.getDuration();
        final double removeAmplitude = form.getAmplitude();
        final int removeNbPoint = form.getNbPoint();

        dataset.removeElement(form);

        for (int i = idx1; i < this.time.getDatas().size(); i++) {
            double timeValue = this.time.getDatas().get(i);
            double value = dataset.getDatas().get(i);
            this.time.getDatas().set(i, Math.max(0, timeValue - removeDuration));
            dataset.getDatas().set(i, value - removeAmplitude);
        }

        for (Element element : dataset.getElements()) {

            int firstIndex = element.getFirstIndex();
            int lastIndex = element.getLastIndex();

            if (firstIndex > idx2) {
                element.setFirstIndex(Math.max(0, firstIndex - removeNbPoint));
                element.setLastIndex(Math.max(0, lastIndex - removeNbPoint));

                element.setT1(this.time.getDatas().get(element.getFirstIndex()));
                element.setT2(this.time.getDatas().get(element.getLastIndex()));
            }

        }

        updateObservateur("Chart");
    }

    public final class Dataset {

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
        }

        private final void removeElement(Element element) {
            this.elements.remove(element);
        }

        public String getName() {
            return name;
        }

        public List<Double> getDatas() {
            return datas;
        }

        public List<Element> getElements() {
            return elements;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            return this.name.equals(obj.toString());
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
