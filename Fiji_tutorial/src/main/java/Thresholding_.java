import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;
import ij.process.AutoThresholder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Double.parseDouble;

public class Thresholding_ implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;
    String[] morphChoice = new String[]{"--select one--",
            "Isolated single pixels disappeared, objects appeared to slightly shrink",
            "Objects that were very close together became separated by a thin line",
            "Small gaps within the objects and at their boundaries were filled in"
    };

    public void beforeSetupDialog(){
        imp = OpenImageHelper.getNLSPombeLowTif();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Thresholding");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("Thresholding is the method by which you can separate " +
                "foreground (signal) pixels from background pixels. The threshold dialog can be accessed from either:",
                        110)+"\n-Image > Adjust > Threshold" +
                "\n- The keyboard shortcut shift + t.");
        gd.addMessage(GdFormatting.addLineBreaks("When the Threshold window is open, pixels above the threshold " +
                "(foreground) will appear red, and pixels below the threshold will not changed color. Test some of the " +
                "different automatic thresholding methods " +
                        "available in the first dropdown menu and see what effect they have on the image.", 110));
        gd.addChoice("Which method did a good job of identifying the nuclei?",
                AutoThresholder.getMethods(), "Default");
        gd.addMessage(GdFormatting.addLineBreaks("Press Reset to remove the threshold. We can make some measurements " +
                "of the image with and without the threshold on. First, let's set the measurements to just be Area and Mean Gray " +
                "Value, and now tick the box next to Limit to threshold. Uncheck all the other boxes, press OK, and then " +
                "measure the image (close any Results windows that are open before doing this!). This will measure all the pixels" +
                " in the image.", 110));
        gd.addMessage(GdFormatting.addLineBreaks("Now, select the threshold method that worked for identifying nuclei. " +
                "Don't press Apply in the Threshold window! Instead, with the red pixel mask on, make another measurement from " +
                "the image.", 110));
        gd.addStringField("What area of the image was above the threshold?", "");
        gd.addButton("Check area answer", this);

        gd.addMessage(GdFormatting.addLineBreaks("Thresholding can be used to convert images into binary masks. " +
                "Set the image threshold so that the nuclei are identified, and then press Apply", 110));
        gd.addRadioButtonGroup("What is the bit depth of the image after pressing Apply?",
                new String[]{"?", "8-bit", "16-bit", "32-bit"}, 1, 4, "?");

        gd.addMessage(GdFormatting.addLineBreaks("From the binary mask, we can measure the shapes " +
                "of the segmented objects. However, first let's try and clean up the mask a bit. Duplicate the binary mask " +
                "so that you have two copies of it to work on. On one copy, go to Process > Binary > Close-. On the other " +
                "copy, go to Process > Binary > Open.", 110));
        gd.addChoice("Which option best describes the effect of the Close operation?",
                morphChoice, morphChoice[0]);
        gd.addChoice("Which option best describes the effect of the Open operation?",
                morphChoice, morphChoice[0]);

        gd.addButton("Press me if you need to re-open the raw data", this);

        gd.addMessage(GdFormatting.addLineBreaks("Close the image that you applied the open operation to. " +
                "Now, select the image that you applied the Close to, and apply the Open operation on top of that. This " +
                "should remove the background single pixels without significantly affecting the other objects.", 110));


        gd.showDialog();
    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        if(awtEvent==null) return true;

        String paramString = awtEvent.paramString();

        String name = "";
        if(awtEvent.getSource() instanceof Choice){
            Choice choice = (Choice) awtEvent.getSource();
            name = choice.getName();
        }


        if(name=="choice0") {
            if (paramString.contains("MaxEntropy") || paramString.contains("RenyiEntropy") || paramString.contains("Yen")) {
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - these three methods seem to do the best " +
                        "job of segmenting the nuclei from this noisy data. You can also manually adjust the threshold using " +
                        "the sliders in the Threshold window, but this isn't ideal for reproducibility. If you can find an " +
                        "automatic method, then this is easier to report in methods. The Otsu thresholding method is " +
                        "commonly used, and is a good method to choose if everything seems to give fairly similar results.", 80));
            } else {
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - there are some methods that better isolate " +
                        "the nuclei (bright round balls) from the rest of the pixels in the image.", 80));
            }
        }
        else if(paramString.contains("bit")){
            if(paramString.contains("8")){
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - thresholding an 8-bit or 16-bit raw " +
                        "image (here it was 16-bit) always results in an 8-bit mask image, where the values are only 0 " +
                        "(pixels below threshold) and 255 (pixels above threshold). If you threshold a 32-bit float " +
                        "image, then you will be given an option to convert to an 8-bit mask similar to what we produced " +
                        "here, or to keep the original values of the pixels that were above the threshold (instead of " +
                        "setting them all to 255) and set the background pixels to NaN ('not a number').", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - resize the window if you can't see " +
                        "the bit depth listed at the top of the image.", 80));
            }
        } else if (name=="choice1") {
            if(paramString.contains(morphChoice[3])){
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - the binary close operation first " +
                        "performs a dilation (the boundaries of all objects become bigger by one pixel) followed by " +
                        "an erosion (the boundaries of all objects are shrunk inwards by one pixel). This can help with " +
                        "noisy edges, like we have here, but if you have objects that are very close together then " +
                        "closing can cause these objects to become merged.", 80));
            }
            else {
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - immediately after applying the " +
                        "close operation, press Ctrl + z to undo it (pressing again will redo it) to see if you can " +
                        "notice the difference.", 80));
            }
        } else if (name=="choice2") {
            if(paramString.contains(morphChoice[1])){
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - the binary open operation first " +
                        "performs an erosion (the boundaries of all objects are shrunk inwards by one pixel) followed " +
                        "by a dilation (the boundaries of all objects are expanded outwards by one pixel). This can " +
                        "remove rogue single pixels that have slipped through the threshold.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - immediately after applying the " +
                        "close operation, press Ctrl + z to undo it (pressing again will redo it) to see if you can " +
                        "notice the difference.", 80));
            }

        }

        return true;
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
        WindowManager.closeAllWindows();
    }

    public static void main(String[] args){
        Class<?> clazz = Thresholding_.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();

        if(paramString.contains("area")){
            String areaString = gd.getNextString();
            if(areaString.isEmpty()) return;
            double area = parseDouble(areaString);
            if(area==64.820 || area==57.076){
                IJ.showMessage("Well done!");
            }
            else if(area==4428.903){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure that you are looking " +
                        "at the right row in your Results table, and that you have made a measurement while the " +
                        "red threshold overlay was covering the pixels.", 80));
            }
            else if(area==4428.903-64.820 || area==4428.903-57.076){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - this is the area below the threshold, " +
                        "not above it.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure that you selected an " +
                        "appropriate automatic threshold method (you can check in the dropdown menu in the exercise " +
                        "window) and that you haven't made any typos.", 80));
            }
        } else if (paramString.contains("raw data")){
            imp = OpenImageHelper.getNLSPombeLowTif();
            imp.show();
        }

    }
}
