import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Open_Mitosis implements PlugIn {
    @Override
    public void run(String s) {
        ImagePlus imp = OpenImageHelper.getLocalMitosisTif();
        imp.show();
    }
}
