import ij.IJ;
import ij.ImagePlus;

import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Exploring_images implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;
    ArrayList<String> wrongSlices = new ArrayList<>(Arrays.asList("104", "99", "105", "110", "116", "101", "106",
            "108", "122", "124", "135", "114", "119", "104", "111", "107"));
    ArrayList<String> wrongHistogram = new ArrayList<>(Arrays.asList("636804", "103.275", "4.459",
            "256", "153.371", "78", "103", "0.332"));

    public void beforeSetupDialog(){

        // load pombe image

        imp = OpenImageHelper.openNup60Stack();

        imp.show();

    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Navigating images in Fiji");
        gd.addDialogListener(this);

        gd.addMessage("An image called 'Nup60_pombe_z-stack.tif' should have opened.");

        gd.addMessage(GdFormatting.addLineBreaks("To zoom in and out of the images you can either use the + and - keys on" +
                " your keyboard, or the magnifying glass in the Fiji toolbar (click to zoom in," +
                " double-click to zoom out.", 80));

        //TODO how to get zoom factor?

        gd.addMessage(GdFormatting.addLineBreaks("At the top of the image, below the title, there is a bar containing information" +
                " about the image. At the bottom, there is a scrollbar for navigating between different images" +
                " in the same window (this is a z stack)", 80));
        gd.addMessage("You can navigate through different images in a single window in the following ways:" +
                "\n- Using the arrow keys on your keyboard" +
                "\n- Using the < (comma) and > (full stop) keys on your keyboard" + "\n"+
                GdFormatting.addLineBreaks("- Using 'Set Slice' to set an exact location. This can be accessed from either the Stk" +
                " button in the Fiji toolbar, or by navigating to Image > Stacks > Set Slice...", 80));


        gd.addMessage(GdFormatting.boldBlue("Move to the 12th image in Nup60_pombe_z-stack.tif"));
        gd.addButton("Check you're in the right place", this);

        gd.addMessage(GdFormatting.addLineBreaks("When you hover over a pixel with your mouse, the location and intensity value of that" +
                " pixel will be displayed in the Fiji toolbar. In Fiji's coordinate system, (0,0) is in the " +
                " top left corner of the image.", 80));
        gd.addStringField("What is the value of pixel (347,611) in the 12th image of Nup60_pombe?", "");
        gd.addButton("Check pixel value answer", this);

        gd.addMessage("Sometimes you might want to inspect the distribution of pixel intensities in an image." +
                "\nThis can be displayed as a histogram - to view the histogram you can either:" +
                "\n- Go to Analyze > Histogram" +
                "\n- Use the keyboard shortcut by pressing h when the image is selected");
        gd.addMessage(GdFormatting.addLineBreaks("Display the histogram. Press 'No' when asked if you want to include all 22 slices to just" +
                " get the histogram of the 12th image.", 80));
        gd.addStringField("What is the highest pixel intensity in the 12th image of Nup60-pombe?", "");
        gd.addButton("Check maximum pixel intensity answer", this);

        gd.addMessage(GdFormatting.addLineBreaks("You can change the display range to make it easier to see" +
                "features in images. This is done via the Brightness/Contrast tool. This can be accessed either:", 80)+"" +
                "\n- From the menus Image > Adjust > Brightness/Contrast..." +
                "\n- Using the keyboard short cut shift + c");
        gd.addRadioButtonGroup("Does changing the brightness and contrast sliders alter the " +
                "pixel values in the image?", new String[]{"?", "Yes", "No", "Only if you press 'Apply'"},
                1, 4, "?");

        gd.showDialog();
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
        imp.close();
    }

    public static void main(String[] args){
        Class<?> clazz = Exploring_images.class;
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

        if(eventString.contains("Yes")){
            IJ.showMessage(GdFormatting.addLineBreaks("Try again - adjusting the sliders shouldn't affect the values stored within" +
                    " the image, just how they are visualised.", 80));
        }
        else if(eventString.contains("No")) {
            IJ.showMessage(GdFormatting.addLineBreaks("Try again - adjusting the sliders shouldn't affect the values stored within" +
                    " the image, just how they are visualised, but there is one dangerous button in the Brightness/Contrast" +
                    " window!", 80));
        }
        else if(eventString.contains("Apply")){
            IJ.showMessage(GdFormatting.addLineBreaks("Well done - adjusting the sliders shouldn't affect the values stored within" +
                    " the image, just how they are visualised. You can confirm this by checking the image histogram." +
                    " However, if you press the Apply button then you will clip your" +
                    " image values to the maximum and minimum of the current display range, which is pretty bad.", 80));
        }

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();

        String pixelValue = gd.getNextString();
        String maxIntensity = gd.getNextString();

        if(paramString.contains("place")){
            int currentSlice = imp.getCurrentSlice();
            if(currentSlice==12){
                IJ.showMessage("Well done - you're on the 12th slice of 22 in the image");
            }
            else{
                IJ.showMessage("Try again");
            }
        }
        else if(paramString.contains("value")){
            if(Objects.equals(pixelValue, "142")){
                IJ.showMessage("Well done!");
            }
            else if(wrongSlices.contains(pixelValue)){
                IJ.showMessage("Try again - check that you are in the 12th image.");
            }
            else{
                IJ.showMessage("Try again - check you are hovering your mouse over the right pixel");
            }
        } else if (paramString.contains("maximum")) {
            if(maxIntensity.equals("163")){
                IJ.showMessage("Well done! If you press the 'Live' button in the histogram window, the histogram" +
                        "\nwill automatically update as you navigate through the image.");
            }
            else if(wrongHistogram.contains(maxIntensity)){
                IJ.showMessage("Try again - check you're reading the correct value from the histogram window");
            }
            else if(maxIntensity.equals("173")){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure you just have a histogram from image 12 and not from" +
                        "across all of the images in the stack.", 50));
            }
            else{
                IJ.showMessage("Try again - make sure you just have the histogram for the 12th image in the window");
            }
        }


    }
}
