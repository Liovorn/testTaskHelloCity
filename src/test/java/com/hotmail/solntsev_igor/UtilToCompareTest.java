package com.hotmail.solntsev_igor;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * @author solntsev igor
 */
public class UtilToCompareTest {

    private static ApplicationContext applicationContext;
    private static HashMap<Locale, HashMap<Integer, String>> localeGreetings;
    private final static int MORNING_KEY = 1;
    private final static int DAY_KEY = 2;
    private final static int EVENING_KEY = 3;
    private final static int NIGHT_KEY = 4;


     //Automatically gets greetings for EN and RU locales. These strings will be used to perform check with messages,
     //obtained from tested class.
     //throws Exception
    @BeforeClass
    public static void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("beans.xml", "helloUtilPrint.xml");
        localeGreetings = new HashMap<>();
        localeGreetings.put(Locale.US, new HashMap<Integer, String>());
        localeGreetings.put(new Locale("ru", "RU"), new HashMap<Integer, String>());

        for (Locale locale : localeGreetings.keySet()) {
            HashMap<Integer, String> greetingsCheckList = localeGreetings.get(locale);
            String morningGreeting, dayGreeting, eveningGreeting, nightGreeting, world;
            try {
                morningGreeting = applicationContext.getMessage("test.greetings.morning", null, locale);
                dayGreeting = applicationContext.getMessage("test.greetings.day", null, locale);
                eveningGreeting = applicationContext.getMessage("test.greetings.evening", null, locale);
                nightGreeting = applicationContext.getMessage("test.greetings.night", null, locale);
                world = applicationContext.getMessage("test.greetings.world", null, locale);
            } catch (NoSuchMessageException e) {
                Runner.logger.error("Unable to get messages");
                throw (e);
            }
            greetingsCheckList.put(1, String.format(UtilToCompare.FORMAT, morningGreeting, world));
            greetingsCheckList.put(2, String.format(UtilToCompare.FORMAT, dayGreeting, world));
            greetingsCheckList.put(3, String.format(UtilToCompare.FORMAT, eveningGreeting, world));
            greetingsCheckList.put(4, String.format(UtilToCompare.FORMAT, nightGreeting, world));
        }
    }

    //Checks if tested class (UtilToCompare) prints appropriate message. EN and RU locales are checked, hours from 0 to 23.
    //throws Exception
    @Test
    public void testPrint() throws Exception {
        UtilToCompare printer = (UtilToCompare) applicationContext.getBean("greetingPrinter");
        for (Locale locale : localeGreetings.keySet()) {
            HashMap<Integer, String> greetingsCheckList = localeGreetings.get(locale);
            String world = applicationContext.getMessage("test.greetings.world", null, locale);
            for (int hourOfDay = 0; hourOfDay < 24; hourOfDay++) {
                String greeting = null;
                if (UtilToCompare.timeCompare(hourOfDay, UtilToCompare.MORNING, UtilToCompare.DAY)) {
                    greeting = applicationContext.getMessage("test.greetings.morning", null, locale);
                } else if (UtilToCompare.timeCompare(hourOfDay, UtilToCompare.DAY, UtilToCompare.EVENING)) {
                    greeting = applicationContext.getMessage("test.greetings.day", null, locale);
                } else if (UtilToCompare.timeCompare(hourOfDay, UtilToCompare.EVENING, UtilToCompare.NIGHT)) {
                    greeting = applicationContext.getMessage("test.greetings.evening", null, locale);
                } else if (UtilToCompare.timeCompare(hourOfDay, UtilToCompare.NIGHT, UtilToCompare.MORNING)) {
                    greeting = applicationContext.getMessage("test.greetings.night", null, locale);
                }
                String checkString = String.format(UtilToCompare.FORMAT, greeting, world);
                PrintStream originalOut = System.out;
                String outputMessage;
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream(25);
                    PrintStream capture = new PrintStream(os);
                    // From this point on, everything printed to System.out will get captured
                    System.setOut(capture);
                    printer.printMessage(hourOfDay, locale);
                    capture.flush();
                    outputMessage = new String(os.toByteArray());
                    assertEquals("Wrong greeting for hour: " + hourOfDay + " and locale: " + locale.toString(),
                            checkString, outputMessage);
                    os.close();
                } finally {
                    System.setOut(originalOut);
                }
            }
        }
    }


     //Checks if UtilToCompare throws an exception when invalid hour parameter is passed (<0);
     //@throws Exception illegalArgumentException for invalid hour value
    @Test(expected = IllegalArgumentException.class)
    public void testLess() throws Exception {
        UtilToCompare printer = (UtilToCompare) applicationContext.getBean("greetingPrinter");
        printer.printMessage(-1, Locale.US);
    }

    //Checks if UtilToCompare throws an exception when invalid hour parameter is passed (>23);
    //@throws Exception illegalArgumentException for invalid hour value
    @Test(expected = IllegalArgumentException.class)
    public void testOver() throws Exception {
        UtilToCompare printer = (UtilToCompare) applicationContext.getBean("greetingPrinter");
        printer.printMessage(24, Locale.US);
    }
}