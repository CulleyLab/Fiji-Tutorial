import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Hyperstack_time implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.openMitosisTif();
        imp.show();

        IJ.setForegroundColor(255, 255, 255);
        IJ.setBackgroundColor(0, 0, 0);
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Hyperstack manipulation: time series");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("Unlike channels and z information, there isn't so much" +
                " to manipulate in terms of the time information in Fiji.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Sometimes, if can be helpful to change the playback speed of a time series in Fiji. " +
                "To do this, right click on the 'play' button to the left of the sliders at the bottom of the image. " +
                "Here, you can change the playback speed (in frames per second)", 80));

        gd.addMessage("Change the playback speed so that there is 100ms between frames");
        gd.addButton("Check my playback speed selection", this);

        gd.addMessage(GdFormatting.addLineBreaks("To display separate time frames as images, you can make them into a montage. " +
                "First, do a maximum intensity projection of mitosis.tif so that we don't have to deal with the " +
                "z slices as well.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Sometimes, you're not interested in a whole time series. Let's " +
                "just duplicate out a portion of the projected frames. You can do this via the keyboard shortcut shift + d, " +
                "or by going to Image > Duplicate...", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Duplicate out a new stack from the maximum intensity projection " +
                "which contains both channels but only contains time frames 21-50", 80));
        gd.addButton("Check the duplication (make sure new stack is active image)", this);

        gd.addMessage(GdFormatting.addLineBreaks("Now, let's make a montage of this subset of frames. " +
                "Go to Images > Stacks > Make Montage...", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Make a 6 x 5 montage of the maximum intensity projection of " +
                "mitosis.tif with a scale factor of 1.00, an increment of 1, a border width of 2 and a font size of 12. " +
                "Make sure 'Label slices' and 'Use foreground color' are checked.", 80));
        gd.addButton("Check my montage (make sure montage is active image)", this);

        gd.showDialog();
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
        WindowManager.closeAllWindows();
    }

    public static void main(String[] args){
        Class<?> clazz = Hyperstack_time.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }
    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();
        ImagePlus thisImp = WindowManager.getCurrentImage();

        if(paramString.contains("playback")){
            double playbackSpeed = thisImp.getCalibration().fps;
            if(playbackSpeed==10.0){
                IJ.showMessage("Well done");
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - the frames per second is equal to 1000ms divided by the " +
                        "target interval between frames in ms", 80));
            }
        }
        else if(paramString.contains("duplication")){
            ImagePlus impActive = WindowManager.getCurrentImage();

            String activeTitle = impActive.getTitle();
            int nChannels = impActive.getNChannels();
            int nFrames = impActive.getNFrames();
            int nSlices = impActive.getNSlices();

            if(nChannels!=2){
                IJ.showMessage(GdFormatting.addLineBreaks("Make sure that you had the 'Duplicate hyperstack' box checked, " +
                        "and that your duplicated image is the currently active image. (I checked image: "+
                        activeTitle+")", 80));
            }
            else if(nFrames==1){
                IJ.showMessage(GdFormatting.addLineBreaks("Make sure that you had the 'Duplicate hyperstack' box checked, " +
                        "and that your duplicated image is the currently active image. (I checked image: "+
                        activeTitle+")", 80));
            }
            else if(nFrames!=30){
                IJ.showMessage(GdFormatting.addLineBreaks("Make sure that you have only duplicated frames 21-50 " +
                        "from the maximum intensity projection. (I checked image: "+activeTitle+")", 80));
            }
            else if(nSlices>1){
                IJ.showMessage(GdFormatting.addLineBreaks("Make sure that you did the maximum intensity projection " +
                        "before duplicating. (I checked image: "+activeTitle+")", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done! Sometimes it's also help to save duplicated portions of images separately for " +
                        "ease of access later, especially if your image was very large. If you have a rectangular ROI on the " +
                        "image when you duplicate it, only the region inside the ROI will be duplicated.", 80));
            }
        }
        else if(paramString.contains("montage")){
            ImagePlus impActive = WindowManager.getCurrentImage();

            String activeTitle = impActive.getTitle();
            int width = impActive.getWidth();
            int nChannels = impActive.getNChannels();
            impActive.setSlice(1);
            float pixelValue1 = impActive.getProcessor().getPixelValue(95, 188);
            float pixelValue2 = impActive.getProcessor().getPixelValue(85, 188);

            if(width<1000 || width>1036){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - do you have 6 columns, and is your scale factor " +
                        "1.0? I checked image: "+activeTitle, 80));
            }
            else if(width>1000 && width<1036){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - did you set the border width to 2? " +
                        "I checked image: "+activeTitle, 80));
            }
            else if(nChannels!=2){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - did you run this on the duplicated " +
                        "maximum intensity projection? I checked image: "+activeTitle, 80));
            }
            else if(pixelValue1 > pixelValue2){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - did you check the Label slices and " +
                        "Use foreground color boxes? I checked image: "+activeTitle, 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done! Some things to be aware of with montages - if you label the slices, like " +
                        "we have done, this overwrites pixel values in the montage image (you can check this by hovering the mouse over the " +
                        "labelled pixels). The increment field in the Make Montage window allows you to skip frames, if you want. For example, " +
                        "an increment of 2 will make the montage using every other frame.", 80));
            }

        }


    }
}
