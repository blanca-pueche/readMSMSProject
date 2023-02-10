import io.github.msdk.MSDKException;

import io.github.msdk.datamodel.IsolationInfo;
import io.github.msdk.datamodel.MsScan;
import io.github.msdk.datamodel.RawDataFile;
import io.github.msdk.io.mzxml.MzXMLFileImportMethod;

import java.io.File;

import java.io.IOException;
import java.util.List;
public class Ejemplo {
    public static void main(String[] args) {
        // Specify the mzXML file path
        File file = new File("C:\\Users\\blanc\\OneDrive\\Escritorio\\METABOLOMICS\\project1\\Plasma_iterative_20ev_4ul-r001.mzXML");
        double precursorMz = 666.6192;

        try {
            // Import the mzXML file
            MzXMLFileImportMethod importer = new MzXMLFileImportMethod(file);
            RawDataFile rawFile = importer.execute();

            int numScans = rawFile.getScans().size();
            System.out.println("num of scans: " + numScans);

            List<MsScan> scans = rawFile.getScans();
            // Iterate through the scans
            for (int i = 0; i < numScans; i++) {
                MsScan scan = scans.get(i);
                List<IsolationInfo> isolations = scan.getIsolations();
                if (isolations.size() > 0) {
                    System.out.println(isolations.get(0).getPrecursorMz());
                }

            }
        }catch(MSDKException e){
                e.printStackTrace();
            }
        }
    }


