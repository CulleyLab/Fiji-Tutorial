import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Double.parseDouble;

public class Measuring_ implements PlugIn, ActionListener, DialogListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.getNLSPombeHighTif();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Measuring in Fiji");
        gd.addDialogListener(this);

        gd.addMessage(GdFormatting.addLineBreaks("Regions of interest (ROIs) are very helpful for " +
                "performing manual measurements of structures in images.", 80));
        gd.addMessage(GdFormatting.addLineBreaks("We'll start by doing some quick measurements. Toggle the " +
                        "line ROI tool in the Fiji toolbar and drag it between the inner edges of the two nuclei in the " +
                        "cell centred at position (170, 178) in the image. As you are dragging, a live angle and length " +
                        "measurement should appear in the Fiji toolbar", 80));
        gd.addStringField("What is the distance you have measured between the inner edges of the nuclei?", "");
        gd.addButton("Check rough inter-nucleus measurement", this);

        gd.addMessage(GdFormatting.addLineBreaks("When we have measurement ROIs, or any ROI on our image, we " +
                "can store them to use again later. Press t on the keyboard to add your line ROI to the ROI manager. You can " +
                "rename ROIs, use them on different images, and save them as files to open again later from the ROI manager.",
                80));
        gd.addButton("Check I stored my ROI", this);

        gd.addMessage(GdFormatting.addLineBreaks("Rather than measuring just by looking at the Fiji toolbar, we can " +
                "ask Fiji to automatically measure for us. First, we need to specify what measurements we want Fiji to make. " +
                "Go to Analyze > Set Measurements... Select the boxes for: Area, Standard deviation, Shape descriptors, and " +
                "Mean gray value. Uncheck all the other boxes.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("You can measure an image by pressing the m key on the keyboard. " +
                "If there is an active ROI on the image, it will restrict the measurements to this, otherwise it will measure the " +
                "whole image. Measure the image along your line ROI by pressing m", 80));

        gd.addButton("Check I made a measurement correctly", this);

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
        Class<?> clazz = Measuring_.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();

        if(paramString.contains("rough")){
            String roughMeasurementString = gd.getNextString();
            if(roughMeasurementString.isEmpty()) return;
            double roughMeasurement = parseDouble(roughMeasurementString);

            if(roughMeasurement<3 || roughMeasurement>5){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure that you are measuring " +
                        "the distance between the inner edges of the cell with two nuclei towards the top left corner" +
                        " of the image.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done - although this is quite an annoying " +
                        "way to measure things in Fiji because the measurement disappears once you release the mouse " +
                        "button. ", 80));
            }
        }
        else if(paramString.contains("stored")){
            RoiManager rm = RoiManager.getInstance();
            if(rm==null){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - there isn't a Roi Manager open." +
                        " please add your" +
                        " ROI to the manager by pressing t on your keyboard. You'll need to have the image with an ROI in " +
                        "it active when you do this.", 80));
            }
            else if(rm.getCount()==0){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - I couldn't find any ROIs in the " +
                        "ROI manager. Please add your ROI to the manager by pressing t on your keyboard. You'll need " +
                        "to have the image with an ROI in it active when you do this.", 80));
            }
            else if(rm.getRoi(0).getType()!=Roi.LINE){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - you have stored a ROI, but it is not " +
                        "a line.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done!", 80));
            }
        }
        else if(paramString.contains("made a measurement")){
            ResultsTable rt = ResultsTable.getActiveTable();

            if(rt==null){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - I couldn't find a results table. Press m," +
                        " or Analyze > Measure to measure along the line profile in the image.", 80));
            }
            else if(rt.getHeadings().length!=9){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - I didn't find the expected number of " +
                        "columns in the results table. Make sure you have checked only the boxes in Set Measurements..." +
                        " that were in the instructions. Make sure you had the line ROI active when making the " +
                        "measurement. (Close the open Results Window before trying again!)", 80));
            }
            else if(rt.getColumn("Area")[rt.size()-1]==4428.903){
                IJ.showMessage(GdFormatting.addLineBreaks("Try again - make sure that you measured along your " +
                        "line ROI and didn't measure the whole image.", 80));
            }
            else{
                IJ.showMessage(GdFormatting.addLineBreaks("Well done! Results tables can be saved as .csv files" +
                        " and opened in other analysis software.", 80));
            }
        }

    }
}
