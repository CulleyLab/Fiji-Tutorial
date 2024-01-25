import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Open_Pombe_Low implements PlugIn {
    @Override
    public void run(String s) {
        ImagePlus imp = OpenImageHelper.getNLSPombeLowTif();
        imp.show();
    }
}
