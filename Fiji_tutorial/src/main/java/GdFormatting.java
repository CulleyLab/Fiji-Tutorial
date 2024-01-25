import ij.IJ;

public class GdFormatting {

    public static String addLineBreaks(String textToBreak, int maxLineLength){

        // break up string by spaces
        String[] splitString = textToBreak.split(" ");

        // create new string for output
        StringBuilder outString = new StringBuilder();

        int lineLength = 0;
        for (String thisString : splitString) {
            int stringLength = thisString.length();
            if (lineLength + stringLength <= maxLineLength){
                if(outString.toString().equals("")) outString.append(thisString);
                else{
                    outString.append(" ").append(thisString);
                }
                lineLength += stringLength + 1;
            }
            else {
                lineLength = stringLength;
                outString.append("\n").append(thisString);
            }
        }
        return(outString.toString());

    }

    public static String addBold(String string){
        return "<b>"+string+"</b>";
    }

    public static String addColour(String string, String colour){
        return "<font color='"+colour+"'>"+string+"</font>";
    }

    public static String addHTML(String string){
        return "<html>"+string+"</html>";
    }

    public static String blue(String string){
        return addHTML(addColour(string, "blue"));
    }

    public static String red(String string){
        return addHTML(addColour(string, "red"));
    }

    public static String bold(String string){
        return addHTML(addBold(string));
    }

    public static String boldRed(String string){
        return addHTML(addColour(addBold(string), "red"));
    }

    public static String boldBlue(String string){
        return addHTML(addColour(addBold(string), "blue"));
    }

    public static void main(String[] args) {
        String testString = "the quick brown fox jumps over the lazy dog";
        int max = 10;
        System.out.println(testString);
        System.out.println(addLineBreaks(testString, max));

        new ij.ImageJ();
        IJ.showMessage(red(testString));
        IJ.showMessage(boldBlue(addLineBreaks(testString, max)));
    }
}

