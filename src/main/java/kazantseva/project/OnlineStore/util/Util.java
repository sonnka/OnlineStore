package kazantseva.project.OnlineStore.util;

import kazantseva.project.OnlineStore.model.entity.enums.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;

public class Util {

    private static final Random random = new Random();
    private static final Rating[] ratings = Rating.values();

    public static Rating randomRating() {
        return ratings[random.nextInt(ratings.length)];
    }

    public static boolean randomAvailable() {
        return random.nextBoolean();
    }

    public static int randomCalories() {
        return (random.nextInt() * ((5000) + 1));
    }

    public static LocalDate randomManufacturingDate() {
        LocalDate start = LocalDate.of(2020, Month.NOVEMBER, 30);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        return start.plusDays(random.nextInt((int) days + 1));
    }

    public static LocalDate randomExpiryDate() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.of(2027, Month.NOVEMBER, 30);
        long days = ChronoUnit.DAYS.between(start, end);
        return start.plusDays(random.nextInt((int) days + 1));
    }

    public static BigDecimal formatPrice(String price) {
        String res = price.split(" ")[0];
        try {
            return BigDecimal.valueOf(Double.parseDouble(res));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static boolean isValidRating(String rating) {
        return Arrays.stream(Rating.values()).map(Enum::name).toList().contains(rating);
    }

    public static boolean isValidMinPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isValidMaxPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isValidMinDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.of(2000, Month.JANUARY, 1));
    }

    public static boolean isValidMaxDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.of(2050, Month.JANUARY, 1));
    }
}
