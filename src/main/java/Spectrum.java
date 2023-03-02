import java.util.List;

public class Spectrum {

    double precursorMz;
    List<Peak> peakList;

    public Spectrum (double precursorMz, List<Peak> peakList){
        this.precursorMz = precursorMz;
        this.peakList = peakList;
    }

}
