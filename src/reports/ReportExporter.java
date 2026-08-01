import java.io.*;

public class ReportExporter {

    private static final String REPORT_FOLDER = "reports";

    public static BufferedWriter createMonthlyReport(int month, int year)
            throws IOException {

        File folder = new File(REPORT_FOLDER);

        if (!folder.exists()) {

            folder.mkdir();

        }

        String monthName = getMonthName(month);

        String fileName =
                REPORT_FOLDER + File.separator
                        + monthName + "_" + year + "_Report.txt";

        BufferedWriter writer =
                new BufferedWriter(new FileWriter(fileName));

        writer.write("==================================================");
        writer.newLine();
        writer.write("MANUFACTURING WORKFLOW MANAGEMENT SYSTEM");
        writer.newLine();
        writer.write("MONTHLY REPORT");
        writer.newLine();
        writer.write("==================================================");
        writer.newLine();
        writer.write("Month : " + monthName + " " + year);
        writer.newLine();
        writer.newLine();

        return writer;

    }

    public static void closeReport(BufferedWriter writer)
            throws IOException {

        if (writer != null) {

            writer.close();

        }

    }

    private static String getMonthName(int month) {

        switch (month) {

            case 1 -> {
                return "January";
            }

            case 2 -> {
                return "February";
            }


            case 3 -> {
                return "March";
            }
            case 4 -> {
                return "April";
            }

            case 5 -> {
                return "May";
            }

            case 6 -> {
                return "June";
            }

            case 7 -> {
                return "July";
            }

            case 8 -> {
                return "August";
            }

            case 9 -> {
                return "September";
            }

            case 10 -> {
                return "October";
            }

            case 11 -> {
                return "November";
            }

            case 12 -> {
                return "December";
            }

            default -> {
                return "Unknown";
            }

        }

    }

}