import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

public class OpenImageHelper {

    public static ImagePlus openMitosisTif(){
        ImagePlus imp = null;
        String[] imageTitles = WindowManager.getImageTitles();

        // no images - open from zip
        if(imageTitles.length==0){
            //imp = IJ.openImage("http://imagej.net/images/Spindly-GFP.zip");
            imp = getLocalMitosisTif();
        }
        // one image
        else if(imageTitles.length==1){
            // image is mitosis image - set as imp
            if(imageTitles[0].startsWith("mitosis")){
                imp = WindowManager.getCurrentImage();
            }
            // image is not mitosis image - open from zip
            else{
                //imp = IJ.openImage("http://imagej.net/images/Spindly-GFP.zip");
                imp = getLocalMitosisTif();
            }
        }
        // more than one image
        else{
            boolean mitosisOpen = false;
            // see if any of the images are mitosis.tif
            for(String title:imageTitles){
                if (title.equals("mitosis.tif")) {
                    mitosisOpen = true;
                    break;
                }
                // identify image and set as tif
                imp = WindowManager.getImage("mitosis.tif");
            }
            // open from zip if not in open images.
            if(!mitosisOpen){
                //imp = IJ.openImage("http://imagej.net/images/Spindly-GFP.zip");
                imp = getLocalMitosisTif();
            }
        }
        return imp;
    }

    public static ImagePlus getNup60Pombe(){
        ClassLoader classLoader = OpenImageHelper.class.getClassLoader();
        String imagePath = "Nup60_pombe_z-stack.tif"; // Adjust the path accordingly

        // Use the class loader to load the image as a resource
        java.net.URL imageURL = classLoader.getResource(imagePath);
        ImagePlus imp = IJ.openImage(imageURL.toString());

        return imp;
    }

    public static ImagePlus getLocalMitosisTif(){
        ClassLoader classLoader = OpenImageHelper.class.getClassLoader();
        String imagePath = "mitosis.tif"; // Adjust the path accordingly

        // Use the class loader to load the image as a resource
        java.net.URL imageURL = classLoader.getResource(imagePath);
        ImagePlus imp = IJ.openImage(imageURL.toString());

        return imp;
    }

    public static ImagePlus getNLSPombeLowTif(){
        ClassLoader classLoader = OpenImageHelper.class.getClassLoader();
        String imagePath = "NLS_pombe_low.tif"; // Adjust the path accordingly

        // Use the class loader to load the image as a resource
        java.net.URL imageURL = classLoader.getResource(imagePath);
        ImagePlus imp = IJ.openImage(imageURL.toString());

        return imp;
    }

    public static ImagePlus getNLSPombeHighTif(){
        ClassLoader classLoader = OpenImageHelper.class.getClassLoader();
        String imagePath = "NLS_pombe_high.tif"; // Adjust the path accordingly

        // Use the class loader to load the image as a resource
        java.net.URL imageURL = classLoader.getResource(imagePath);
        ImagePlus imp = IJ.openImage(imageURL.toString());

        return imp;
    }

    public static void main(String[] arg){
        new ij.ImageJ();
        ImagePlus imp1 = getLocalMitosisTif();
        ImagePlus imp2 = getNup60Pombe();

        imp1.show();
        imp2.show();

    }
}
