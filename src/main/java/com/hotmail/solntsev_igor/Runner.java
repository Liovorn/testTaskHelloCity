package com.hotmail.solntsev_igor;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.UnsupportedEncodingException;

/**
 * @author solntsev igor
 */
public class Runner {

    final static Logger logger = Logger.getLogger(Runner.class);

    public static void main(String[] args) throws UnsupportedEncodingException {

        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"beans.xml", "helloUtilPrint.xml"});

        UtilToCompare printer = (UtilToCompare) context.getBean("greetingPrinter");
        logger.info("Runner printer created");
        try {
            printer.printMessage();
            logger.info("Runner printed Ok");
        } catch (IllegalArgumentException iae) {
            logger.error("Invalid time entered");
        } catch (NoSuchMessageException nsme) {
            logger.fatal("Cannot get messages");
        }

    }
}
