import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Open_Pombe_ZStack implements PlugIn {
    @Override
    public void run(String s) {
        ImagePlus imp = OpenImageHelper.getNup60Pombe();
        imp.show();
    }
}
