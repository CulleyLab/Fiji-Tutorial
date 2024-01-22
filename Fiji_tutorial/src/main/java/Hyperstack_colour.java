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

public class Hyperstack_colour implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        // try and open current active image
        imp = WindowManager.getCurrentImage();

        // no currently open image - reload mitosis.tif
        if(imp == null){
            imp = IJ.openImage("http://imagej.net/images/Spindly-GFP.zip");
            imp.show();
        }
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Manipulating hyperstacks: colour");

        gd.addMessage("We'll keep working on the image 'mitosis.tif' for this exercise." +
                "\nIf it isn't open, you can find it by going to File > Open Samples > Mitosis (5D Stack)");

        gd.addMessage("");

        gd.addMessage("For multi-channel images, you can choose to view them in a number of different ways." +
                "\nOpen the Channels tool dialog either from Image > Color > Channels Tool... or by using" +
                "\nthe keyboard shortcut shift + z." );

        gd.addMessage("The drop down menu shows different view options." +
                "'Composite' displays all channels overlaid, 'Color' displays the channels separately (you have" +
                "\nto change the channel slider to view each channel), and 'Grayscale' displays the channels separately" +
                "\nwithout any look-up table.");

        gd.addMessage("Set the display mode to 'Color', and only display channel 2");
        gd.addMessage("You can change the look-up table from the Fiji toolbar. Close the Channels tool," +
                "\nand press the LUT button on the Fiji toolbar. Change the look-up table to Cyan.");

        gd.addButton("Press to confirm you are only displaying channel 2, in cyan", this);

        gd.addMessage("If you ever need to separate out different channels from a multicolour image" +
                "\nstack, then a quick way of doing this is from Image > Color > Split Channels");
        gd.addButton("Press to confirm that you split the channels", this);

        gd.showDialog();

    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
    }

    public static void main(String[] args){
        Class<?> clazz = Hyperstack_colour.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();

        if(paramString.contains("cyan")){
            if(imp.getCompositeMode()!=2){
                IJ.showMessage("Check what display mode you are in using the Channels tool...");
                return;
            }
            if(imp.getChannel()!=2){
                IJ.showMessage("Check that channel 2 is currently active...");
                return;
            }
            if(!imp.getLuts()[1].toString().contains("cyan")){
                IJ.showMessage("Check whether you changed the LUT of channel 2 to cyan...");
                return;
            }
            IJ.showMessage("Well done! You have successfully manipulated the displayed colours.");
        }

        if(paramString.contains("split")){
            int nImages = WindowManager.getImageCount();
            if(nImages < 2){
                IJ.showMessage("Please make sure you had mitosis.tif selected when you pressed" +
                        "\nSplit Channels");
                return;
            }
            else{
                String[] imageTitles = WindowManager.getImageTitles();
                boolean containsC1 = false;
                boolean containsC2 = false;
                for(String imageTitle:imageTitles){
                    if(imageTitle.startsWith("C1")) containsC1 = true;
                    if(imageTitle.startsWith("C2")) containsC2 = true;
                }
                if(containsC1 && containsC2){
                    IJ.showMessage("Well done - you have successfully split the channels of this image." +
                            "\nNote that each channel stack still has all the original time frames and z slices.");
                    return;
                }
                else{
                    IJ.showMessage("Try again - if you accidentally closed mitosis.tif, then you can" +
                            "\nreopen it by going to File > Open Samples > Mitosis (5D stack)");
                    return;
                }
            }
        }

    }
}
