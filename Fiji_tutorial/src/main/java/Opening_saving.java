import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;

public class Opening_saving implements PlugIn {

    NonBlockingGenericDialog gd;

    public void setupDialog(){
        gd = new NonBlockingGenericDialog("Opening and saving images");

        gd.addMessage(GdFormatting.addLineBreaks("You can either open images in Fiji by going to File > " +
                "Open, or by dragging an image onto the Fiji toolbar.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Standard file formats like .tif will open straight away, " +
                "like we've seen in this tutorial. Proprietary microscope file formats, like .nd2, .lsm etc will bring up " +
                "the 'Bio-Formats Importer' dialog. In this you can specify, for example, to only import a certain range of " +
                "frames of a dataset.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("When dealing with very large files in Fiji (e.g. with lots of z slices or frames), you can open " +
                "them as a virtual stack to save memory. Virtual stacks don't read in the whole dataset in one go; instead they " +
                "load each slice on-the-fly as you navigate through the image stack. However, operations that apply to the whole " +
                "stack (e.g. duplicating the stack) may take a while as it will then still need to read everything. There is an " +
                "option in the Bio-Formats importer to 'Use virtual stack'.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("Sometimes Fiji will say it doesn't have enough memory to open " +
                "a dataset. You can either use the Virtual stack opening in Bio-Formats importer, or you can try and allocate " +
                "more memory to Fiji. This is done by going to Edit > Options > Memory and Threads.... In this window you can " +
                "increase the amount of memory used by Fiji (restart Fiji to confirm). You can also clear the cache in a Fiji " +
                "session by double-clicking on the message bar in the Fiji toolbar.", 80));

        gd.addMessage(GdFormatting.addLineBreaks("When exporting your final flattened RGB images for presentation, " +
                "I recommend doing so in either .png, .gif or .jpeg file format. Fiji automatically saves files as .tif, so you need to " +
                "go to File > Save As... to save in a non-tif format. You can export RGB stacks as movies by saving as AVI in this " +
                "menus, or as Animated Gifs (select Animated Gif... from the Save As menu, not just gif).", 80 ));

        gd.addMessage(GdFormatting.addLineBreaks("If you want to save intermediate images from analysis, for example " +
                "binary masks or filtered data, that you might use again, these should be saved as .tif files. The .tif file " +
                "format is capable of storing non-RGB data (i.e. 16-bit, 32-bit images) and the metadata of your image.", 80));
        gd.showDialog();
    }
    @Override
    public void run(String s) {
        setupDialog();
    }
}
