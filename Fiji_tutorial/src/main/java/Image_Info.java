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
        imp = IJ.openImage("http://imagej.net/images/Spindly-GFP.zip");
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Exploring properties of hyperstacks");
        gd.addDialogListener(this);

        gd.addMessage("Hyperstacks are 4-dimensional or 5-dimensional images." +
                "\nThe following questions all refer to the window 'mitosis.tif', which should have opened automatically");

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
        gd.addMessage("Normally, images have much more complex metadata with all of the properties of the microscope and acquisition settings used.");
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
                IJ.showMessage("Try again - image dimensions are displayed at the top of the image in the" +
                        "\norder width x height.");
            }
        }

        // check if they did channel correctly
        if(eventString.contains("channel")){
            if(eventString.contains("2")){
                IJ.showMessage("Correct! This image has two channels." +
                        "\nThe green channel is a tubulin stain, the red channel is labelled Aurora.");
            }
            else{
                IJ.showMessage("Try again - channels are usually the different colours in an image." +
                        "\nClue: Fiji indicates number of channels with the letter 'c' at the top of the image...");
            }
        }

        // check if they did frames correctly
        if(eventString.contains("frames")){
            if(eventString.contains("51")){
                IJ.showMessage("Correct! This image has 51 frames." +
                        "\nThe frames show the progression of mitosis over time.");
            }
            else{
                IJ.showMessage("Try again - frames are the different time points at which both channels and" +
                        " a z-stack were acquired. \nClue: Fiji indicates number of frames with the letter " +
                        "'t' (for time) at the top of the image.");
            }
        }

        // check if they did slices correctly
        if(eventString.contains("slices")){
            if(eventString.contains("51") || eventString.contains("2")){
                IJ.showMessage("Try again - slices are the different focal depths that were imaged." +
                        "\nClue: Fiji indicates number of slices with the letter 'z' at the top of the image.");
            }
            else{
                IJ.showMessage("Correct! This image has 5 z-slices." +
                        "\nThe slices shows the different depths at which the cell was imaged.");
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
                IJ.showMessage("Correct! This is a 16-bit hyperstack. Every image in this stack" +
                        " is a 16-bit image. \nThis means that the minimum value a pixel can have is 0, and the" +
                        "\n maximum pixel value is 65535 (2^16 - 1). The pixels cannot contain negative values" +
                        "\n nor non-whole numbers. Bit depth is always shown at the top of an image (for small" +
                        "\n images you may have to zoom in with the + key to see this!)");
            }
            else{
                IJ.showMessage("Try again - Bit depth is always shown at the top of an image (for small\n" +
                                   "n images you may have to zoom in with the + key to see this!)");
            }
        }

        if(paramString.contains("pixel")){
            if(pixelSize.contains("0.0885")){
                IJ.showMessage("Correct! Pixel width and height are almost always going to be equal and have the" +
                        "\n same unit. Other commonly seen units are um and microns (which Fiji knows to calibrate as " +
                        "\n micrometers), nm, and 'pixels' for images where Fiji cannot find pixel calibration metadata." +
                        "\n You can change the pixel size manually in the Properties window if for some reason Fiji has got it wrong.");
            }
            else{
                IJ.showMessage("Try again - make sure you are looking at the pixel width or pixel height field in the" +
                        "\n Properties window, and that you have the decimal point in the right place.");
            }

        }

        if(paramString.contains("slice")){
            if(Objects.equals(sliceDepth, "1") || sliceDepth.contains("1.0")){
                IJ.showMessage("Correct! The voxel depth tells you how far apart z slices are. The default voxel depth" +
                        "\n is 1.0000000 - this will be displayed if there is no appropriate z calibration information" +
                        "\n in the image metadata. However, here, the slice depth is indeed 1µm.");
            }
            else{
                IJ.showMessage("Try again - make sure you are looking at the voxel depth field in the" +
                        "\nProperties window. A voxel is the 3D equivalent of a pixel, which is 2D.");
            }
        }

        if(paramString.contains("time")){
            if(Objects.equals(timeInterval, "0.14")){
                IJ.showMessage("Correct! A two-colour z-stack was acquired every 0.14 seconds");
            }
            else{
                IJ.showMessage("Try again - make sure you are looking at the 'Frame interval' field in the" +
                        "\nProperties window");
            }
        }
    }
}
