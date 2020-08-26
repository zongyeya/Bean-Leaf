package com.syp.model;

// Data Structure Import
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.time.DateUtils.isSameDay;

// Class holding static methods to filter an ArrayList<Order>
public class FilterOrders {

    // --------------------------------
    // Filter Orders by specified Date
    // --------------------------------
    private static ArrayList<Order> filterOrderByDate(ArrayList<Order> userOrder, Date todayDate) {

        ArrayList<Order> userTodayOrder = new ArrayList<>();
        for(Order o: userOrder) {

            if(isSameDay(todayDate, o.getTimestampAsDate())) {
                userTodayOrder.add(o);
            }
        }
        return userTodayOrder;
    }

    // ------------------------------
    // Filter Orders by today's date
    // ------------------------------
    public static ArrayList<Order> filterOrderByToday(ArrayList<Order> userOrder) {
        return FilterOrders.filterOrderByDate(userOrder, new Date());
    }

    // ------------------------------
    // Filter Orders by this week
    // ------------------------------
    public static ArrayList<Order> filterOrderByCurrentWeek(ArrayList<Order> userOrder) {
        ArrayList<Order> userThisWeekOrder = new ArrayList<>();

        for(int i = 0; i < userOrder.size(); i++) {
            if(isSameWeek(new Date(), userOrder.get(i).getTimestampAsDate())) {
                userThisWeekOrder.add(userOrder.get(i));
            }
        }
        return userThisWeekOrder;
    }

    // ------------------------------
    // Filter Orders by this month
    // ------------------------------
    public static ArrayList<Order> filterOrderByCurrentMonth(ArrayList<Order> userOrder) {

        ArrayList<Order> userThisMonthOrder = new ArrayList<>();
        for(Order o: userOrder) {

            if(isSameMonth(new Date(), o.getTimestampAsDate())) {
                userThisMonthOrder.add(o);
            }

        }
        return userThisMonthOrder;
    }

    // ----------------------------------------
    // Check if two dates are in the same week
    // ----------------------------------------
    private static boolean isSameWeek(Date date1, Date date2) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR));
    }

    // ----------------------------------------
    // Check if two dates are in the same month
    // ----------------------------------------
    private static boolean isSameMonth(Date date1, Date date2) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);

    }

    // --------------------------------------
    // Get map of item to item total
    // --------------------------------------
    public static Map<String, Float> getOrdersItemTotalMap(ArrayList<Order> orders){

        // Declare mapping of String - Double
        HashMap<String, Float> itemTotalMapping = new HashMap<>();

        for(Order o: orders) {

            // Get items in each order
            List<Item> itemsInOrder = o.getItemsAsList();
            for(Item i: itemsInOrder) {

                // Update or create value depending
                if(itemTotalMapping.containsKey(i.getName()))
                    itemTotalMapping.put(i.getName(),itemTotalMapping.get(i.getName()) + i.getPrice().floatValue());
                else
                    itemTotalMapping.put(i.getName(),i.getPrice().floatValue());
            }
        }

        return itemTotalMapping;
    }

    // -------------------------------------------
    // Get map of weekday this week to item total
    // -------------------------------------------
    public static Map<String, Float> getOrdersDayAmountForWeekMap(ArrayList<Order> orders){

        // Dec
        HashMap <String, Float> dayAmountMap = new HashMap<>();

        // For each order
        for(Order o: orders) {
            // Get weekday string (Sun, Mon ...)
            String weekdayOfOrder = DateFormats.getWeekDayString(o.getTimestampAsDate());
            // Check existence and add / create price total entry
            if(!dayAmountMap.containsKey(weekdayOfOrder))
                dayAmountMap.put(weekdayOfOrder, (float)o.getTotalSpent());
            else
                dayAmountMap.put(weekdayOfOrder, dayAmountMap.get(weekdayOfOrder) + (float)o.getTotalSpent());

        }

        return dayAmountMap;
    }

    // --------------------------------------
    // Get map of date to item total
    // --------------------------------------
    public static Map<String, Float> getOrdersDayAmountForMonthMap(ArrayList<Order> orders){

        // Dec
        HashMap <String, Float> dayAmountMap = new HashMap<>();

        // For each order
        for(Order o: orders) {
            // Get weekday string (Sun, Mon ...)
            String dateString = DateFormats.getDateString(o.getTimestampAsDate());
            // Check existence and add / create price total entry
            if(!dayAmountMap.containsKey(dateString))
                dayAmountMap.put(dateString, (float)o.getTotalSpent());
            else
                dayAmountMap.put(dateString, dayAmountMap.get(dateString) + (float)o.getTotalSpent());

        }

        return dayAmountMap;
    }

    // --------------------------------------
    // Get total $ spent in a list of orders
    // --------------------------------------
    public static double getOrdersTotal(ArrayList<Order> orders){
        double total = 0;
        for(Order o: orders){
            total += o.getTotalSpent();
        }
        return total;
    }

}
