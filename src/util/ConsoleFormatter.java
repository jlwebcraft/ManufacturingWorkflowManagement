public class ConsoleFormatter {

    public static String padRight(String text, int length) {

        if (text == null) {
            text = "";
        }

        if (text.length() > length) {
            text = text.substring(0, length - 3) + "...";
        }

        StringBuilder builder = new StringBuilder(text);

        while (builder.length() < length) {
            builder.append(" ");
        }

        return builder.toString();
    }

}