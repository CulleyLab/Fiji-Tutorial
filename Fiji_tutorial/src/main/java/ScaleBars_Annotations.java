import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.*;
import ij.plugin.PlugIn;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ScaleBars_Annotations implements PlugIn, ActionListener, DialogListener {
    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.get3ColourImage();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Adding scale bars and annotations");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("You can add various annotations to images using Fiji. " +
                "The most important of these is, of course, the scale bar.", 80));
        gd.addMessage(GdFormatting.addLineBreaks("Add a scale bar to the image by going to Analyze > Tools >" +
                " Scale Bar.... Play around with some of the settings, but before you press OK make sure you have the " +
                "'Overlay' box ticked.", 80));
        gd.addButton("Check the scale bar is added to the Overlay", this);

        gd.addMessage(GdFormatting.addLineBreaks("You can add ROIs to the overlay. Double click the line button " +
                "in the toolbar and select the arrow tool. Draw an arrow on the image, and press b on the keyboard (or go " +
                "to Image > Overlay > Add selection...). Before adding the ROI to the overlay, you can select its colour by double clicking the colour dropper in the " +
                "Fiji toolbar.", 80));
        gd.addMessage(GdFormatting.addLineBreaks("As well as ROI shapes, you can also add text to the overlay. "+
                "The capital A in the Fiji toolbar is the text tool, and double clicking on this allows you to set font and " +
                "size. Drag a text box onto the image, type something meaningful in it, and then go to Image > Overlay > Add " +
                "Selection... or press Ctrl + b to add it to the overlay.", 80));
        gd.addButton("Check my Overlay contains an arrow and some text (it can contain other stuff as well!)", this);
        gd.addMessage(GdFormatting.addLineBreaks("To store the contents of the overlay into the image," +
                " go to Image > Overlay > Flatten (or use keyboard shortcut shift + f). This compresses the overlay and " +
                "any colour channels into a single image.", 80));
        gd.addChoice("What is the type of the image after flattening?",
                new String[]{"--select one--", "8 bit", "16 bit", "32 bit", "RGB colour"}, "--select one--");
        gd.showDialog();
    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        if(awtEvent==null){return true;}

        String paramString = awtEvent.paramString();

        String name = "";
        if(awtEvent.getSource() instanceof Choice){
            Choice choice = (Choice) awtEvent.getSource();
            name = choice.getName();
        }
        if(name.equals("choice0")){
            if(paramString.contains("8")){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - look at the top bar of the image.", 80));
            }
            else if(paramString.contains("16")){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - look at the top bar of the image.", 80));
            }
            else if(paramString.contains("32")){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - look at the top bar of the image.", 80));
            }
            else if(paramString.contains("RGB")){
                IJ.showMessage(GdFormatting.addLineBreaks("Well done! Once an image has been flattened to RGB, " +
                        "it can be saved in JPEG and PNG formats without losing information. You should flatten images with " +
                        "overlays to RGB even if they only have one colour channel. However, you can't convert back " +
                        "from RGB to 16 bit, so you should not do any analysis on images after they have been flattened " +
                        "to RGB.", 80));
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
        Class<?> clazz = ScaleBars_Annotations.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String paramString = e.paramString();

        if(paramString.contains("scale")){
            Overlay overlay = imp.getOverlay();
            if(overlay==null){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - did you " +
                        "definitely have the 'Overlay' box ticked before pressing OK in the scale bar dialog?", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - the Overlay is a layer that lives on " +
                        "top of the image data that can contain shapes and text to annotate an image. The Overlay does not " +
                        "alter the pixel values in the image and can be hidden and shown through the Image > Overlay menu.", 80));
            }
        }
        else if(paramString.contains("arrow")){
            Overlay overlay = imp.getOverlay();
            if(overlay==null){
                IJ.showMessage("Try again - there isn't anything in the overlay of this image.");
                return;
            }

            Roi[] overlayRois = overlay.toArray();
            boolean foundArrow = false;
            boolean foundText = false;
            for(Roi roi:overlayRois){
                if(roi.toString().contains("Arrow")) foundArrow = true;
                if(roi.toString().contains("Text")) foundText = true;
            }
            if(!foundArrow){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - have you added an arrow to the overlay " +
                        "of your image?", 80));
            }
            else if(!foundText){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - have you added some text to the overlay " +
                        "of your image?", 80));
            }
            else{
                IJ.showMessage("Well done!");
            }
        }

    }

}
