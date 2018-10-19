package utils;

import java.io.File;

import javax.swing.JFileChooser;

public abstract class Utilitaire {

    public final static String TXT = "txt";
    public final static String CYCLE = "cycle";

    /*
     * Get the extension of a file.
     */
    public static final String getExtension(File f) {

        final String s = f.getName();

        final int i = s.lastIndexOf('.');

        return (i > 0 && i < s.length() - 1) ? s.substring(i + 1).toLowerCase() : "";
    }

    public static final String getFileNameWithoutExtension(File f) {

        final String fileNameWithExtension = f.getName();

        final int i = fileNameWithExtension.lastIndexOf('.');

        return (i > 0 && i < fileNameWithExtension.length() - 1) ? fileNameWithExtension.substring(0, i) : "";
    }

    public static final String cutNumber(String number) {
        return number.indexOf(".") < 0 ? number : number.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    public static final boolean isNumber(String s) {

        final String REGEX_NUMBER_SI = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
        // final String REGEX_NUMBER = "[+-]?\\d+(\\.\\d+)?";

        return s.matches(REGEX_NUMBER_SI);
    }

    public static final String getFolder(String title, String defautPath) {
        final JFileChooser fileChooser = new JFileChooser(defautPath);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        final int reponse = fileChooser.showDialog(null, "Select");
        if (reponse == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        }
        return defautPath;
    }
}
