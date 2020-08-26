package com.syp.test;

import com.syp.model.DateFormats;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DateFormatsModelTest {

    DateFormats WhiteBoxDateFormats;
    @Before
    public void setUp() throws Exception {
        WhiteBoxDateFormats = new DateFormats();

    }

    @Test
    public void weekDayTest() throws ParseException {


        String sDate1="24/11/2019";
        Date date =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);

        assertEquals("Sun", WhiteBoxDateFormats.getWeekDayString(date));
    }

    @Test
    public void getDayString() throws ParseException {
        String sDate1="24/11/2019";
        Date date =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);

        assertEquals("Nov 24", WhiteBoxDateFormats.getDateString(date));

    }

    @After
    public void tearDown() throws Exception {
    }
}