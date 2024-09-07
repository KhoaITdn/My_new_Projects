package OOP.ss7;

import java.time.LocalDate;

public class NextDayCalculator {
    public static LocalDate calculateNexDate(LocalDate localDate) {
        return localDate.plusDays(1);

    }
}
