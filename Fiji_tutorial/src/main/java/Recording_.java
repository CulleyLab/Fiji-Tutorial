import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

public class Recording_ implements PlugIn {

    NonBlockingGenericDialog gd;

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Using the Recorder");

        gd.addMessage(GdFormatting.addLineBreaks("Fiji has a really useful function for keeping track of " +
                "what you have done to images. If you go to Plugins > Macros > Record... this will bring up the Recorder " +
                "window. If you set the menu next to 'Record' to 'Macro', it will record the steps in a way that you could " +
                "turn into an automatic macro in the future.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Pressing 'Create' will open the contents of the Recorder into the " +
                "Fiji code editor. You can save this as a .txt or .ijm (ImageJ Macro language) format with analysed data as " +
                "a reminder of the steps that you took, or code around it to make a macro for e.g. batch processing.", 80));

        gd.showDialog();

    }
    @Override
    public void run(String s) {
        setupDialog();
    }
}
