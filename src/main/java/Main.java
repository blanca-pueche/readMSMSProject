

import io.github.msdk.MSDKException;

import io.github.msdk.datamodel.MsScan;
import io.github.msdk.datamodel.RawDataFile;
import io.github.msdk.io.mzxml.MzXMLFileImportMethod;
import org.apache.commons.math3.util.Precision;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // Specify the mzXML file path
        File file = new File("C:\\Users\\blanc\\OneDrive\\Escritorio\\METABOLOMICS\\project1\\Plasma_iterative_20ev_4ul-r001.mzXML");
        double precursorMz = 666.6192;

        try {
            // Import the mzXML file
            MzXMLFileImportMethod importer = new MzXMLFileImportMethod(file);
            RawDataFile rawFile = importer.execute();


            // Get the number of scans
            int numScans = rawFile.getScans().size();
            System.out.println("num of scans: " + numScans);

            // Iterate through the scans
            for (int i = 0; i < numScans; i++) {
                MsScan scan = rawFile.getScans().get(i);
                int size = scan.getIsolations().size();
                if (size > 0) {
                    double number = scan.getIsolations().get(0).getPrecursorMz();
                    boolean comp1 = Math.abs(number-precursorMz)<0.1;
                    if (scan.getMsLevel() == 2 && (comp1)) {
                        // Print the scan properties
                        System.out.println("Retention time: " + scan.getRetentionTime());
                        System.out.println("Precursor m/z: " + scan.getIsolations().get(0).getPrecursorMz());
                        System.out.println("Number of data points: " + scan.getNumberOfDataPoints());
                        // Print the data points
                        for (int j = 0; j < scan.getNumberOfDataPoints(); j++) {
                            System.out.println("m/z: " + scan.getMzValues()[j] + " intensity: " + scan.getIntensityValues()[j]);
                        }
                    }
                }
            }


        } catch (MSDKException e) {
            e.printStackTrace();
        }
    }
}