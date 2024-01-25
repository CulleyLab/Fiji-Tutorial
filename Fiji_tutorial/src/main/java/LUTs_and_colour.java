import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class LUTs_and_colour implements PlugIn, DialogListener, ActionListener {
    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.get3ColourImage();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Lookup tables and colour");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("When displaying multicolour images, it is important to " +
                "take accessibility into account. Two- and three- colour images can be difficult to interpret with " +
                "colour-blindness. Fiji has the function to simulate various types of colour blindness. This is found in " +
                "Image > Color > Simulate Color Blindness. Flatten this " +
                "three channel image, and see what happens to the visibility of the channels when you simulate different " +
                "types of colour blindness (the red and green variations are most common)", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Press the button below to open an article on best practices in " +
                "presenting multi-colour microscopy images:", 80));
        gd.addButton("Open ASCB Scientific Figures Accessibility Article", this);

        gd.addMessage(GdFormatting.addLineBreaks("From the article, choose one of the three-colour look up " +
                "table combinations and adjust this image to be colour-blindness friendly. Flatten it, and check with " +
                "one of the Simulate Color Blindness options.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("When displaying multi-colour images, you should also display " +
                "grayscale images of the individual channels. The quickest way to do this is to select your multi-channel " +
                "hyperstack and then go to Image > Color > Split Channels. You can then set the look up table to Grays.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Finally, if you want to display the a bar showing the pixel " +
                "intensities in a single colour image, you can do this by going to Analyze > Tools > Calibration bar... This can be added " +
                "to the Overlay along with scale bars and other annotations. Try this on one of the split channels.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Fiji has a lot of look up table options, and it can be " +
                "tempting to choose something jazzy like Red Hot rather than boring Grays for single channel images. This " +
                "is not best practice, as most of the exciting look up tables in Fiji are not perceptually uniform.", 80));

        gd.addButton("Link to information about perceptual uniformity, if you're interested", this);

        gd.showDialog();
    }
    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        if(awtEvent==null) return true;
        String paramString = awtEvent.paramString();

        return true;
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
    }

    public static void main(String[] args){
        Class<?> clazz = LUTs_and_colour.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();
        if(paramString.contains("Open")){
            try {
                openWebpage(new URL("https://www.ascb.org/science-news/how-to-make-scientific-figures-accessible-to-readers-with-color-blindness/"));
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if(paramString.contains("perceptual")){
            try {
                openWebpage(new URL("https://colorcet.com/"));
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
