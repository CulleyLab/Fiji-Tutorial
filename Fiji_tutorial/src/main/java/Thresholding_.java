import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Thresholding_ implements PlugIn, DialogListener, ActionListener {

    NonBlockingGenericDialog gd;
    ImagePlus imp;

    public void beforeSetupDialog(){
        imp = OpenImageHelper.getNLSPombeLowTif();
        imp.show();
    }

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Thresholding");


    }

    @Override
    public boolean dialogItemChanged(GenericDialog genericDialog, AWTEvent awtEvent) {
        return true;
    }

    @Override
    public void run(String s) {
        beforeSetupDialog();
        setupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
