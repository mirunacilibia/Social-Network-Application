package domain.validators;

import java.time.LocalDate;

public class ValidatorDates {
    public static boolean validateDates(LocalDate dateStart, LocalDate dateEnd) {
        if(dateEnd==null || dateStart==null)
            return false;
        return dateStart.compareTo(dateEnd) <= 0 && dateEnd.compareTo(LocalDate.now())<=0 && dateStart.compareTo(LocalDate.now())<=0;
    }
}
