import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/***
 * The tool assumes the weekly BU is on Monday.
 *
 * ### Syntax ###
 * start end keep_monthly keep_weekly keep_daily
 * DD/MM/YYYY DD/MM/YYYY n k l
 *
 * java org.example.Main 12/05/2022 02/08/2024 6 4 7
 */

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate start = LocalDate.parse(args[0], formatter);
            LocalDate end = LocalDate.parse(args[1], formatter);
            int monthly = Integer.parseInt(args[2]);
            int weekly = Integer.parseInt(args[3]);
            int daily = Integer.parseInt(args[4]);

            DateTimeFormatter form = DateTimeFormatter.ofPattern("dd.LLL.yy");

            ArrayList<String> monthlyBackups = new ArrayList<>();
            ArrayList<String> weeklyBackups = new ArrayList<>();
            ArrayList<String> dailyBackups = new ArrayList<>();

            for(LocalDate date = start;
                date.isBefore(end.plusDays(1));
                date = date.plusDays(1)) {

                // Do specific Backup
                if(date.getDayOfMonth() == 1) {
                    monthlyBackups.add(date.format(form)); // append Backup to list
                } else if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    weeklyBackups.add(date.format(form));
                } else {
                    dailyBackups.add(date.format(form));
                }

                applyRetentionPolicies(dailyBackups, weeklyBackups, monthlyBackups, date, daily, weekly, monthly, form);
            }
        System.out.println("#### MONTHLY BACKUPS ####");
        System.out.println("Days: " + monthlyBackups);
        System.out.println("Total: " + monthlyBackups.size());

        System.out.println("\n #### WEEKLY BACKUPS ####");
        System.out.println("Days: " + weeklyBackups);
        System.out.println("Total: " + weeklyBackups.size());

        System.out.println("\n #### DAILY BACKUPS ####");
        System.out.println("Days: " + dailyBackups);
        System.out.println("Total: " + dailyBackups.size());


        } catch (NumberFormatException | DateTimeException e) {
            throw new RuntimeException(e);
        }
    }

    private static void applyRetentionPolicies(
            ArrayList<String> dailyBackups,
            ArrayList<String> weeklyBackups,
            ArrayList<String> monthlyBackups,
            LocalDate currentDate, int dailyRetention, int weeklyRetention, int monthlyRetention, DateTimeFormatter form) {
        // Daily backups retention
        LocalDate dailyRetentionThreshold = currentDate.minusDays(dailyRetention);
        dailyBackups.removeIf(backup -> LocalDate.parse(backup, form).isBefore(dailyRetentionThreshold));

        // Weekly backups retention
        LocalDate weeklyRetentionThreshold = currentDate.minusWeeks(weeklyRetention);
        weeklyBackups.removeIf(backup -> LocalDate.parse(backup, form).isBefore(weeklyRetentionThreshold));

        // Monthly backups retention
        LocalDate monthlyRetentionThreshold = currentDate.minusMonths(monthlyRetention);
        monthlyBackups.removeIf(backup -> LocalDate.parse(backup, form).isBefore(monthlyRetentionThreshold));
    }
}
