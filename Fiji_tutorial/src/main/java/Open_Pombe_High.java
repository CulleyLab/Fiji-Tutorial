import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Open_Pombe_High implements PlugIn {
    @Override
    public void run(String s) {
        ImagePlus imp = OpenImageHelper.getNLSPombeHighTif();
        imp.show();
    }
}
