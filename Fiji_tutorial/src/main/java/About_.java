import ij.IJ;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class About_ implements PlugIn, ActionListener {
    NonBlockingGenericDialog gd;

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("About this tutorial plugin");

        gd.addMessage(GdFormatting.addLineBreaks("This tutorial is made and maintained by Siân Culley at King's College London :-) " +
                "It will be hopefully updated and improved over time. You can report bugs or suggest features using " +
                "the buttons below", 80));

        gd.addButton("Provide feedback via GitHub Issues", this);
        gd.addButton("Send Siân an email", this);
        gd.addButton("Complain about this tutorial", this);

        gd.showDialog();
    }
    @Override
    public void run(String s) {
        setupDialog();
    }

    public static void main(String[] args){
        Class<?> clazz = About_.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        new ij.ImageJ();

        IJ.runPlugIn(clazz.getName(),"");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String paramString = e.paramString();
        if(paramString.contains("GitHub")){
            try {
                LinksHelper.openWebpage(new URL("https://github.com/CulleyLab/Fiji-Tutorial/issues"));
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if(paramString.contains("email")){
            Desktop desktop = Desktop.getDesktop();
            String message = "mailto:sian.culley@kcl.ac.uk?subject=Feedback%20on%20Fiji%20Tutorial";
            URI uri = URI.create(message);
            try {
                desktop.mail(uri);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if(paramString.contains("Complain")){
            try {
                LinksHelper.openWebpage(new URL("https://screamintothevoid.com/"));
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
