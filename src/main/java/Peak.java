public class Peak {

    double mz;
    double intensity;

    public Peak(double mz, double intensity){
        this.mz = mz;
        this.intensity = intensity;
    }

    public double getIntensity(){
        return this.intensity;
    }
}
