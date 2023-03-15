

import io.github.msdk.MSDKException;

import io.github.msdk.datamodel.MsScan;
import io.github.msdk.datamodel.RawDataFile;
import io.github.msdk.io.mzxml.MzXMLFileImportMethod;
import org.apache.commons.math3.util.Precision;
import scala.xml.Null;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        // Specify the mzXML file path
        //File file = new File("C:\\Users\\blanc\\OneDrive\\Escritorio\\METABOLOMICS\\project1\\Plasma_iterative_20ev_4ul-r001.mzXML");
        double precursorMz = 666.6192;
        String pathFile = "C:\\Users\\blanc\\OneDrive\\Escritorio\\METABOLOMICS\\project1\\Plasma_iterative_20ev_4ul-r001.mzXML";

        try {
            getSpectrumFromMZML(pathFile, precursorMz, 10);

        } catch (MSDKException e) {
            e.printStackTrace();
        }
    }

    //returns the maximum intensity within the fragment
    public static double maximum_value (float[] array){
        float max_value = 0;
        for (int i = 0; i< array.length; i++){
            if(array[i] > max_value){
                max_value = array[i];
            }
        }
        return max_value;
    }

    /**
     * Returns the tolerance in Daltons for a search
     *
     * @param theoreticalMass mass of the compound of interest === precursorMz
     * @param tolerancePPM Tolerance allowed for the mass spectrometer
     */
    public static double calculateToleranceFromPPM(Double theoreticalMass, int tolerancePPM) {
        double range;
        range = theoreticalMass * (tolerancePPM/1000000);
        return range;
    }

    public static List<Spectrum> getSpectrumFromMZML ( String pathFile, double precursorMz, int toleranceppm) throws IllegalArgumentException, MSDKException {
        if(precursorMz <= 0 || toleranceppm < 0 || toleranceppm >100){
            IllegalArgumentException ex = new IllegalArgumentException();
            throw ex;
        }
        double range = calculateToleranceFromPPM(precursorMz, toleranceppm);
        File file = new File(pathFile);

        MzXMLFileImportMethod importer = new MzXMLFileImportMethod(file);
        RawDataFile rawFile = importer.execute();
        List<Spectrum> spectrumList = new ArrayList<Spectrum>();
        List<Peak> peakList = new ArrayList<Peak>();

        // Get the number of scans
        int numScans = rawFile.getScans().size();
        //System.out.println("num of scans: " + numScans);

        // Iterate through the scans
        for (int i = 0; i < numScans; i++) {
            MsScan scan = rawFile.getScans().get(i);
            int size = scan.getIsolations().size();
            peakList = null;
            if (size > 0) {
                double number = scan.getIsolations().get(0).getPrecursorMz();
                //tolerancia

                boolean comp1 = Math.abs(number-precursorMz)<range;

                if (scan.getMsLevel() == 2 && (comp1)) {
                    // Print the scan properties

                    float[] intensity_values = scan.getIntensityValues();

                    double maximum_intensity = maximum_value(intensity_values);
                    double values_taken_from = 0.01*maximum_intensity;
                    //System.out.println("------------------------------------------------"+values_taken_from);

                    System.out.println("Retention time: " + scan.getRetentionTime());
                    System.out.println("Precursor m/z: " + scan.getIsolations().get(0).getPrecursorMz());
                    System.out.println("Number of data points: " + scan.getNumberOfDataPoints());
                    // Print the data points
                    for (int j = 0; j < scan.getNumberOfDataPoints(); j++) {
                        boolean comp2 =  scan.getIntensityValues()[j] >= values_taken_from;
                        if (comp2) {
                            //System.out.println("m/z: " + scan.getMzValues()[j] + " intensity: " + scan.getIntensityValues()[j]);
                            Peak peak = new Peak(scan.getMzValues()[j], scan.getIntensityValues()[j]);
                            peakList.add(peak);
                        }
                    }
                }
                Spectrum spectrum = new Spectrum(scan.getIsolations().get(0).getPrecursorMz(), peakList);
                spectrumList.add(spectrum);
            }
        }
        return spectrumList;
    }

    public static Spectrum getSpectrumRelativeIntensity (Spectrum spectrumNoFilters, int mileage) throws IllegalArgumentException{
        List<Peak> listaPicos = null;
        Peak pico;
        if (mileage < 0 || mileage >1000){
            IllegalArgumentException ex = new IllegalArgumentException();
            throw ex;
        }
        double maxIntensity = max_value(spectrumNoFilters);
        double value = maxIntensity*(mileage/100);
        for (int i=0; i<spectrumNoFilters.peakList.size(); i++){
            if (spectrumNoFilters.peakList.get(i).intensity < value){
                pico = new Peak(spectrumNoFilters.peakList.get(i).mz, spectrumNoFilters.peakList.get(i).intensity);
                listaPicos.add(pico);
            }
        }
        Spectrum spectrum = new Spectrum(spectrumNoFilters.precursorMz, listaPicos);
        return spectrum;
    }

    public static double max_value (Spectrum spectrum){
        double maxIntensity = 0;
        for (int i=0; i<spectrum.peakList.size(); i++){
            if (spectrum.peakList.get(i).intensity > maxIntensity){
                maxIntensity = spectrum.peakList.get(i).intensity;
            }
        }
        return maxIntensity;
    }

    public static Spectrum getSpectrumAboveThreshold (Spectrum spectrumNoFilters, double threshold) throws IllegalArgumentException{
        try{
            List<Peak> listaPicos = null;
            Peak pico;
            for (int i = 0; i< spectrumNoFilters.peakList.size(); i++){
                if (spectrumNoFilters.peakList.get(i).intensity < threshold){
                    pico = new Peak(spectrumNoFilters.peakList.get(i).mz, spectrumNoFilters.peakList.get(i).intensity);
                    listaPicos.add(pico);
                }
            }
            Spectrum spectrum = new Spectrum(spectrumNoFilters.precursorMz, listaPicos);
            return spectrum;
        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

    public static Spectrum getSpectrumTopNPeak (Spectrum spectrumNoFilters, int topNPeaks) throws IllegalArgumentException{
        try{
            List<Peak> listaPicos = null;
            Peak pico;
            int n=0;
            Collections.sort(spectrumNoFilters.peakList, Comparator.comparingDouble(Peak::getIntensity));
            for (int i = spectrumNoFilters.peakList.size(); n < topNPeaks; i--){
                pico = new Peak(spectrumNoFilters.peakList.get(i).mz, spectrumNoFilters.peakList.get(i).intensity);
                listaPicos.add(pico);
                n++;
            }
            Spectrum spectrum = new Spectrum(spectrumNoFilters.precursorMz, listaPicos);
            return spectrum;
        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

    /*public static Spectrum getAverageSpectrum (List<Spectrum> spectrumList) throws IllegalArgumentException{
        try{



        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }*/

}