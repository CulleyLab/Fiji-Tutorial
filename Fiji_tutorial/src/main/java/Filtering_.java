import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageStatistics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Double.parseDouble;

public class Filtering_ implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.getNLSPombeLowTif();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Image filtering");
        gd.addDialogListener(this);

        gd.addButton("Click me to reload the original image", this);

        gd.addMessage(GdFormatting.addLineBreaks("Sometimes we'll want to process our images before analysis " +
                "(because noise in the image is making analysis inaccurate) or for visual clarity. The simplest form of " +
                "image processing is filtering", 110));

        gd.addMessage(GdFormatting.addLineBreaks("Fiji's pre-installed filters live in Process > Filters. There " +
                "are several filtering options, but for demonstration we're just going to use Gaussian filtering.",
                110));

        gd.addMessage(GdFormatting.addLineBreaks("On the NLS_pombe_low.tif image, explore the effect of Gaussian " +
                "filtering (Process > Filters > Gaussian blur...) by previewing filters of different sizes (the default " +
                "is that these sizes are entered in pixels)", 110));

        gd.addMessage("A small radius Gaussian blur can be used to reduce single pixel shot noise in images.");

        gd.addStringField("What is the maximum Gaussian blur radius you can use before the structures (" +
                "nuclei) start to appear larger?", "");
        gd.addButton("Check maximum structure-maintaining radius answer", this);

        gd.addMessage(GdFormatting.addLineBreaks("If you applied the above blur, then use the button at the " +
                "top of this window to re-open the raw image.", 110));

        gd.addMessage("A large radius Gaussian blur can be used to identify background in images.");

        gd.addStringField("What is the smallest Gaussian blur radius you can use for the nucleus to blend into " +
                "the cell background fluorescence?", "");
        gd.addButton("Check smallest background-defining radius answer", this);

        gd.addMessage(GdFormatting.addLineBreaks("We can combine the results of a large Gaussian blur and " +
                "a small Gaussian blur to remove both pixel-level noise and background. Duplicate two copies of the raw " +
                "image, and rename (Image > Rename... or right-click on image and select from menu) one as 'small blur' " +
                "and the other as 'large blur'.", 110));
        gd.addMessage(GdFormatting.addLineBreaks("Apply a noise-reducing small radius Gaussian blur to " +
                "'small blur' and a background-defining large radius Gaussian blur to 'large blur'.", 110));
        gd.addMessage(GdFormatting.addLineBreaks("Now, we will subtract the background from the de-noised " +
                "foreground. Go to Process > Image Calculator... and set up this window so that you are performing " +
                "'small blur' Subtract 'large blur'. Check the 'Create new window' box, but do not check the '32-bit " +
                "float result' box.", 110));
        gd.addMessage(GdFormatting.addLineBreaks("This process is known as a Difference-of-Gaussians (DoG) filter", 110));
        gd.addButton("Check my DoG-filtered image", this);

        gd.showDialog();
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
        WindowManager.closeAllWindows();
    }

    public static void main(String[] args){
        Class<?> clazz = Filtering_.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        if(awtEvent == null) return true;

        String paramString = awtEvent.paramString();

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();

        gd.resetCounters();
        String maxGoodGaussianString = gd.getNextString();
        String smallestLargeGaussianString = gd.getNextString();

        if(paramString.contains("reload")){
            imp = OpenImageHelper.getNLSPombeLowTif();
            imp.show();
            return;
        }

        if(paramString.contains("maximum")){

            if(maxGoodGaussianString.isEmpty()) return;
            double maxGoodGaussian = parseDouble(maxGoodGaussianString);

            if(maxGoodGaussian<2){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - you can blur slightly more than this before you start " +
                        "to affect the size of the nuclei in this image. It might help to zoom in on one or two " +
                        "nuclei while you adjust the blur size.", 80));
            }
            else if(maxGoodGaussian>3){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - this blur size is making the nuclei " +
                        "seem larger than they were in the raw data. It might help to zoom in on one or two nuclei" +
                        " while you adjust the blur size.", 80));
            }
            else if (maxGoodGaussian>=2 && maxGoodGaussian<=3){
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - this seems to be a good compromise between reducing " +
                        "noise on the pixel level and maintaining structure.", 80));
            }
//            else if (maxGoodGaussian==3){
//                IJ.showMessage(GdFormatting.addLineBreaks("This is in the right ballpark - I'd personally go slightly smaller, but " +
//                        "the size increase at a radius of 3 is quite subjective and so I think this would still be an " +
//                        "acceptable compromise between noise reduction and maintaining structure.", 80));
//            }
        }
        else if(paramString.contains("smallest")){
            if(smallestLargeGaussianString.isEmpty()) return;
            double smallestLargeGaussian = parseDouble(smallestLargeGaussianString);

            if(smallestLargeGaussian<12){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - at this radius, the nucleus is still " +
                        "quite prominent within the cells.", 80));
            }
            else if(smallestLargeGaussian>15){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - this radius does indeed blur out the nucleus entirely, but " +
                        "the blur is so large that it is almost totally flattening the image and removing all structure.", 80));
            }
            else if(smallestLargeGaussian>=12 && smallestLargeGaussian <=15){
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - this seems to be a good compromise " +
                        "between removing the structure we're interested in without also flattening the background " +
                        "fluorescence into nothing", 80));
            }
        }
        else if(paramString.contains("DoG")){
            ImagePlus impActive = WindowManager.getCurrentImage();
            String activeTitle = impActive.getTitle();
            ImageStatistics statistics = impActive.getStatistics();
            double minPixel = statistics.min;
            double maxPixel = statistics.max;
            int bitDepth = impActive.getBitDepth();

            if(bitDepth!=16){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - I was expecting a 16-bit image as the " +
                        "result. Did you accidentally tick the '32-bit float result' box in the Image Calculator " +
                        "dialog? I checked image: "+activeTitle , 80));
            }
            else if(minPixel>0){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - did you select 'Subtract' as the operation in the " +
                        "Image Calculator dialog? I checked image: "+activeTitle , 80));
            }
            else if(maxPixel<20 || maxPixel>25){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - did you used Gaussian filter values within " +
                        "the suggested ranges from the first part of this exercise? I checked image: "
                        + activeTitle, 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - you have successfully performed a " +
                        "DoG filter on the image. Compare it and its histogram with the raw image - the values will have " +
                        "changed, but you should be able to see the nuclei with better contrast. If you ever perform any " +
                        "filtering on your images prior to analysis, this must be reported in your Methods. Here, you would " +
                        "report e.g. 'Images were filtered using a difference-of-Gaussians filter with a small radius of 2 " +
                        "and a large radius of 12. As a guide for estimating the filter sizes to use, the small radius should " +
                        "be approximately the spatial resolution of your image, so you don't lose detail, and the large radius " +
                        "should be bigger than the largest object that is part of the structure (e.g. here the largest nucleus).", 80));
            }


        }
    }

}
