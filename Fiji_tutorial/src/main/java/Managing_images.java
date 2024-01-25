import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

public class Managing_images implements PlugIn {

    NonBlockingGenericDialog gd;

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Managing lots of images");

        gd.addMessage(GdFormatting.addLineBreaks("Sometimes in Fiji you'll find yourself overwhelmed with the " +
                "number of image windows you have open, especially if you are creating intermediate processed images for " +
                "analysis. The Window menu has a couple of options for cleaning things up - Tile will zoom out your images " +
                "so they can all be fitted onto the screen, and Cascade will put them all on top of each other, slightly " +
                "offset so that you can see the titles.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("If you are creating intermediate images (e.g. filtered images, " +
                "binary masks etc) I strongly recommend duplicating your raw data before applying any processing operations. " +
                "Firstly, this avoids you accidentally saving a processed image and overwriting the raw data. Secondly, Fiji " +
                "only has an Undo buffer size of 1 - i.e. you can only undo the last operation with Ctrl + z, nothing further " +
                "back. When duplicating, you will make life much easier if you give your images sensible name !", 80));

        gd.addMessage(GdFormatting.addLineBreaks("When you exit Fiji, if you've done lots of processing it might go " +
                "through all your images individually asking if you want to save changes. This can be annoying if you're certain " +
                "you've already saved what you need. To get around this, the keyboard shortcut shift + w will close all the windows " +
                "(but without dialog to confirm, so only use if you're certain). You can then exit Fiji quickly.", 80 ));

    }
    @Override
    public void run(String s) {

    }
}
