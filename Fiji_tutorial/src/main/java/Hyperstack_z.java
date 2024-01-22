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

public class Hyperstack_z implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.openMitosisTif();
        imp.show();
    }

    public void setupDialog(){

        gd = new NonBlockingGenericDialog("Hyperstack manipulation: z stacks");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("We're still sticking with our trusty friend, mitosis.tif," +
                " which you should still have open. Please make sure it is the currently active window!", 80));

        gd.addMessage("Occasionally, you will want to project 3D data as a 2D image. In Fiji," +
                "\nthis is called z projection.");
        gd.addMessage("Z Projection can be accessed in two ways:" +
                "\n- Image > Stacks > Z Project..." +
                "\n- The 'Stk' button on the toolbar > Z Project...");

        gd.addMessage(GdFormatting.addLineBreaks("Make an average intensity Z projection and a maximum intensity Z projection" +
                " of mitosis.tif (keep 'all time frames' ticked)", 80));
        gd.addButton("Check you have made the projections correctly", this);

        gd.addRadioButtonGroup("Which projection method looks brighter?",
                new String[]{"?", "Average is brighter", "Max is brighter", "Both same brightness"},
                2, 2, "?");

        gd.addRadioButtonGroup("Which projection method looks sharper?",
                new String[]{"?", "Average is sharper", "Max is sharper", "Both same sharpness"},
                2, 2, "?");

        gd.addRadioButtonGroup("What happens if you do an average intensity z projection of" +
                "\nyour maximum intensity projected stack (MAX_mitosis.tif)?",
                new String[]{"?", "Nothing", "The channels are averaged", "The frames are averaged"},
                2, 2, "?");

        gd.addMessage(GdFormatting.addLineBreaks("Fiji can display images in 3D, but it is slow to render " +
                "and doesn't look very nice. If you want to do this (on the original mitosis.tif image, without " +
                "any z projection), this is found in Image > Stacks > 3D Project... I don't really recommend " +
                "using Fiji for 3D visualisation. For axial (Z) information, it can be more helpful to look at the " +
                "XZ and YZ projections of the stack. This can be done (again, for the original mitosis.tif image) " +
                "by going to Image > Stacks > Orthogonal Views (keyboard shortcut shift + h). You can drag the " +
                "yellow crosshair around to get different slices through the data.", 80));

        gd.showDialog();
    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        if(awtEvent == null) return true;

        String eventString = awtEvent.paramString();

        if(eventString.contains("brighter")){
            if(eventString.contains("Max")){
                IJ.showMessage("Well done - maximum intensity projections will have higher pixel values than" +
                        "\naverage projections, as for each pixel in the image contains the value of the brightest" +
                        "\npixel for that channel/frame across all z slices. The average intensity projection is the" +
                        "\nmean pixel value across all z slices for that channel/frame.");
            }
            else{
                IJ.showMessage("Try again - if you adjusted the brightness or contrast of your images" +
                        "\nto match each other, see which image has the higher pixel values.");
            }
        }
        else if(eventString.contains("sharper")){
            if(eventString.contains("Max")){
                IJ.showMessage("Well done - maximum intensity projections often look sharper, as ideally" +
                        "\nthe brightest pixels are the ones that are best in focus. However, maximum intensity" +
                        "\nprojections can suffer if there are very bright out-of-focus objects in addition to" +
                        "\nthe structure of interest, or if there are 'hot' pixels in the camera chip which have" +
                        "\nconstantly high intensities.");
            }
            else{
                IJ.showMessage("Try again - compare the appearance of the green channel between the two at" +
                        "\ntime point 31 (you may need to adjust brightness/contrast.");
            }
        }

        else if(eventString.contains("Nothing")){
                IJ.showMessage("Try again - make sure you had the MAX_mitosis.tif image active when you" +
                        "\nran the z projection.");
            }
        else if(eventString.contains("channels are averaged")){
                IJ.showMessage("Try again - look at the sliders in the projected image and the numbers in" +
                        "\nthe image information bar");
        }
        else if(eventString.contains("frames are averaged")){
                IJ.showMessage("Well done. If a hyperstack contains multiple z slices, these will be projected." +
                        "\nIf there is only one z slices, but multiple time frames, Fiji will project the time frames." +
                        "\nFiji should never project channels together.");
        }

        return true;
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
    }

    public static void main(String[] args){
        Class<?> clazz = Hyperstack_z.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();
        if(paramString.contains("projection")){
            String[] imageTitles = WindowManager.getImageTitles();
            boolean maxDone = false;
            boolean avgDone = false;
            String maxTitle = "";
            String avgTitle = "";
            for(String title:imageTitles){
                if(title.startsWith("AVG")){
                    avgDone = true;
                    avgTitle = title;
                }
                if(title.startsWith("MAX")){
                    maxDone = true;
                    maxTitle = title;
                }
            }
            if(!maxDone){
                IJ.showMessage("Make sure you have made a maximum intensity projection of mitosis.tif");
                return;
            }
            if(!avgDone){
                IJ.showMessage("Make sure you have made an average intensity projection of mitosis.tif");
                return;
            }

            ImagePlus impMax = WindowManager.getImage(maxTitle);
            int nChannelsMax = impMax.getNChannels();
            int nFramesMax = impMax.getNFrames();

            if(nChannelsMax!=2 || nFramesMax != 51){
                IJ.showMessage("Make sure that you did your maximum intensity projection when mitosis.tif" +
                        "\nwas the active image");
                return;
            }

            ImagePlus impAvg = WindowManager.getImage(avgTitle);
            int nChannelsAvg = impAvg.getNChannels();
            int nFramesAvg = impAvg.getNFrames();

            if(nChannelsAvg!=2 || nFramesAvg!=51){
                IJ.showMessage("Make sure that you did your average intensity projection when mitosis.tif" +
                        "\nwas the active image");
                return;
            }

            IJ.showMessage("Well done - you produced maximum and average intensity projections of mitosis.tif." +
                    "\nNote that you still have both channels and all time frames present in both images.");
        }

    }
}
