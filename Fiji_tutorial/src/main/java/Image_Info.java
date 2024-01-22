import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class Image_Info implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.openMitosisTif();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Exploring properties of hyperstacks");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("Hyperstacks are 4-dimensional or 5-dimensional images. " +
                "The following questions all refer to the window 'mitosis.tif', which should have opened automatically", 80));

        gd.addMessage("");

        gd.addRadioButtonGroup("How many pixels wide is this image?",
                new String[]{"?", "15.13 pixels wide", "17.35 pixels wide", "171 pixels wide",
                "196 pixels wide"}, 1,5, "?");

        gd.addRadioButtonGroup("How many channels are in this image?",
                new String[]{"?", "2 channels", "5 channels", "51 channels"},
                1, 3, "?");

        gd.addRadioButtonGroup("How many time points (frames) are in this image?",
                new String[] {"?", "2 frames", "5 frames", "51 frames"},
                1, 3, "?");

        gd.addRadioButtonGroup("How many z planes (slices) are in this image?",
                new String[] {"?", "2 slices", "5 slices", "51 slices"},
                1, 3, "?");

        gd.addMessage("");

        gd.addStringField("How many images are in this hyperstack in total?", "");
        gd.addButton("Check number of images answer", this);

        gd.addStringField("What is the bit depth of this hyperstack?", "");
        gd.addButton("Check bit depth answer", this);

        gd.addMessage("");

        //TODO: this might be OS dependent
        gd.addMessage("You can inspect the properties of an image by the following methods:" +
                "\n - Going to the 'Image' menu, then 'Properties...'" +
                "\n - Pressing shift + p when an image is the active window");

        gd.addStringField("What is the pixel size in this hyperstack (in µm)?", "");
        gd.addButton("Check pixel size answer", this);

        gd.addStringField("What is the distance between z slices in this hyperstack (in µm)>", "");
        gd.addButton("Check z slice answer", this);

        gd.addStringField("How long is there between time points (in seconds)?", "");
        gd.addButton("Check time interval answer", this);

        gd.addMessage("");

        gd.addMessage("You can also inspect the metadata associated with an image by either:" +
                "\n - Going to the 'Image' menu, then 'Show Info..." +
                "\n - Pressing the i key when an image is the active window");
        gd.addMessage(GdFormatting.addLineBreaks("Normally, images have much more complex metadata with all of the properties of " +
                "the microscope and acquisition settings used.", 80));
        gd.showDialog();

    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
    }

    public static void main(String[] args){
        Class<?> clazz = Image_Info.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        if(awtEvent == null) return true;

        String eventString = awtEvent.paramString();

        // check if they did size correctly
        if(eventString.contains("wide")){
            if(eventString.contains("171")){
                IJ.showMessage("Well done! The image is 171 pixels wide and 196 pixels high.");
            }
            else if(eventString.contains(".")){
                IJ.showMessage("Try again - the questions want number of pixels, not calibrated dimensions");
            }
            else if(eventString.contains("196")){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - image dimensions are displayed at the top of the image in " +
                        "the order width x height.", 80));
            }
        }

        // check if they did channel correctly
        if(eventString.contains("channel")){
            if(eventString.contains("2")){
                IJ.showMessage(GdFormatting.addLineBreaks("Correct! This image has two channels. The green channel is a tubulin " +
                        "stain, the red channel is fluorescently-labelled Aurora.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - channels are usually the different colours in an image. " +
                        "Clue: Fiji indicates number of channels with the letter 'c' at the top of the image...", 80));
            }
        }

        // check if they did frames correctly
        if(eventString.contains("frames")){
            if(eventString.contains("51")){
                IJ.showMessage("Correct! This image has 51 frames." +
                        "\nThe frames show the progression of mitosis over time.");
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - frames are the different time points " +
                        "at which both channels and" +
                        " a z-stack were acquired. Clue: Fiji indicates number of frames with the letter " +
                        "'t' (for time) at the top of the image.", 80));
            }
        }

        // check if they did slices correctly
        if(eventString.contains("slices")){
            if(eventString.contains("51") || eventString.contains("2")){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - slices are the different focal depths that were imaged." +
                        " Clue: Fiji indicates number of slices with the letter 'z' at the top of the image.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Correct! This image has 5 z-slices." +
                        " The slices shows the different depths at which the cell was imaged.", 80));
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String nImages = gd.getNextString();
        String bitDepth = gd.getNextString();

        String pixelSize = gd.getNextString();
        String sliceDepth = gd.getNextString();
        String timeInterval = gd.getNextString();

        String paramString = e.paramString();
        if(paramString.contains("images")){
            if(Objects.equals(nImages, "510")){
                IJ.showMessage("Correct! The number of images in a hyperstack is equal to" +
                        " channels * slices * frames");
            }
            else{
                IJ.showMessage("Try again - clue: this number of images in a hyperstack is" +
                        " equal to channels * slices * frames");
            }
        }

        if(paramString.contains("bit")){
            if(Objects.equals(bitDepth, "16")){
                IJ.showMessage(GdFormatting.addLineBreaks("Correct! This is a 16-bit hyperstack. Every image " +
                        "in this stack" +
                        " is a 16-bit image. This means that the minimum value a pixel can have is 0, and the" +
                        " maximum pixel value is 65535 (2^16 - 1). The pixels cannot contain negative values" +
                        " nor non-whole numbers. Bit depth is always shown at the top of an image (for small" +
                        " images you may have to zoom in with the + key to see this!)", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - Bit depth is always shown at the top " +
                        "of an image (for small images you may have to zoom in with the + key to see this!)", 80));
            }
        }

        if(paramString.contains("pixel")){
            if(pixelSize.contains("0.0885")){
                IJ.showMessage(GdFormatting.addLineBreaks("Correct! Pixel width and height are almost always " +
                        "going to be equal and have the" +
                        " same unit. Other commonly seen units are um and microns (which Fiji knows to calibrate as " +
                        " micrometers), nm, and 'pixels' for images where Fiji cannot find pixel calibration metadata." +
                        " You can change the pixel size manually in the Properties window if for some reason Fiji has got it wrong.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure you are looking at the pixel width or " +
                        "pixel height field in the" +
                        " Properties window, and that you have the decimal point in the right place.", 80));
            }

        }

        if(paramString.contains("slice")){
            if(Objects.equals(sliceDepth, "1") || sliceDepth.contains("1.0")){
                IJ.showMessage(GdFormatting.addLineBreaks("Correct! The voxel depth tells you how far apart " +
                        "z slices are. The default voxel depth" +
                        " is 1.0000000 - this will be displayed if there is no appropriate z calibration information" +
                        " in the image metadata. However, here, the slice depth is indeed 1µm.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure you are looking at the " +
                        "voxel depth field in the" +
                        "Properties window. A voxel is the 3D equivalent of a pixel, which is 2D.", 80));
            }
        }

        if(paramString.contains("time")){
            if(Objects.equals(timeInterval, "0.14")){
                IJ.showMessage("Correct! A two-colour z-stack was acquired every 0.14 seconds");
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure you are looking at the " +
                        "'Frame interval' field in the Properties window", 80));
            }
        }
    }
}
