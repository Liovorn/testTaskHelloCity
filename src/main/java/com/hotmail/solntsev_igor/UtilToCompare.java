package com.hotmail.solntsev_igor;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

/**
 * @author solntsev igor
 */
public class UtilToCompare implements MessageSourceAware {

    private MessageSource messageSource;
    public static final String FORMAT = "%s, %s!";
    public static final int MORNING = 6;
    public static final int DAY = 9;
    public static final int EVENING = 19;
    public static final int NIGHT = 23;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //method to print greeting message
    public void printMessage() {
        Calendar calendar = new GregorianCalendar();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        Locale systemLocale = Locale.getDefault();

        printMessage(hourOfDay, systemLocale);
    }

     //method to print message with parameters
    public void printMessage(int hourOfDay, Locale locale) throws NoSuchMessageException {
        Objects.requireNonNull(locale, "Illegal argument: locale cannot be null");
        if (hourOfDay > 23 || hourOfDay < 0) {
            throw new IllegalArgumentException("Invalid hour argument");
        }

        String greeting = null;
        String world;
        try {
            if (timeCompare(hourOfDay, MORNING, DAY)) {
                greeting = messageSource.getMessage("test.greetings.morning", null, locale);
            } else if (timeCompare(hourOfDay, DAY, EVENING)) {
                greeting = messageSource.getMessage("test.greetings.day", null, locale);
            } else if (timeCompare(hourOfDay, EVENING, NIGHT)) {
                greeting = messageSource.getMessage("test.greetings.evening", null, locale);
            } else if (timeCompare(hourOfDay, NIGHT, MORNING)) {
                greeting = messageSource.getMessage("test.greetings.night", null, locale);
            }
            world = messageSource.getMessage("test.greetings.world", null, locale);
        } catch (NoSuchMessageException e) {
            throw e;
        }

        System.out.print(String.format(FORMAT, greeting, world));
    }

    //method to get range of timezine
    public static boolean timeCompare(int number, int from, int to) {
        if (from > to) return (number >= from && number < 24) || (number >= 0 && number < to);
        else return from <= number && number < to;
    }

}
