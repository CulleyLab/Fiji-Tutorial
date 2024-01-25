import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Open_FluoCells implements PlugIn {
    @Override
    public void run(String s) {
        ImagePlus imp = OpenImageHelper.get3ColourImage();
        imp.show();
    }
}
