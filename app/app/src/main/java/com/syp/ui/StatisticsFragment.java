//Package
package com.syp.ui;

// Fragment imports
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Data Structure imports
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Android View Imports
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// Grpahing API imports
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

// Import Package
import com.syp.MainActivity;
import com.syp.model.DateFormats;
import com.syp.model.FilterOrders;
import com.syp.model.Order;
import com.syp.R;
import com.syp.model.Singleton;
import com.syp.model.User;

// ---------------------------------------------------------
// Fragment for page showing all user statistics and graphs
// ---------------------------------------------------------
public class StatisticsFragment extends Fragment {

    // Colors for grpahs
    private int lightGreen = Color.rgb(102, 255, 178);
    private int gray = Color.rgb(230, 230, 230);
    private int lightRed = Color.rgb( 255, 102, 102 );

    // Views & Activities
    public MainActivity mainActivity;
    public View v;
    public TextView dailyTotal;
    public TextView weeklyTotal;
    public TextView monthlyTotal;
    public TextView totalCaffeine;
    public TextView totalSpendingText;
    public PieChart caffeinePieChart;
    public BarChart dailySpendingBarChart;
    public BarChart weeklySpendingBarChart;
    public BarChart monthlySpendingBarChart;


    // ---------------------------------------
    // On Create (Fragment Override Required)
    // ---------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate View with statistics fragment
        v = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Find all views
        mainActivity = (MainActivity) this.getActivity();
        dailyTotal = v.findViewById(R.id.totalDay);
        weeklyTotal = v.findViewById(R.id.totalWeek);
        monthlyTotal = v.findViewById(R.id.totalMonth);
        totalCaffeine = v.findViewById(R.id.caffeineTotal);
        caffeinePieChart = v.findViewById(R.id.pieChartCaffeine);
        dailySpendingBarChart = v.findViewById(R.id.barChartPriceDaily);
        weeklySpendingBarChart = v.findViewById(R.id.barChartPriceWeekly);
        monthlySpendingBarChart = v.findViewById(R.id.barChartPriceMonthly);
        totalSpendingText = v.findViewById(R.id.totalSpending);

        // Add event
        fetchUser();
        return v;
    }

    // ------------------------------------------------------------------------------------------
    // Add Event Listener when we retrieve / update a value for User class (Firebase Interaction)
    // -------------------------------------------------------------------------------------------
    public void fetchUser(){

        // Get reference in database
        DatabaseReference userRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId());

        // Add on Change listener for reference
        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get snapshot as User object
                User user = dataSnapshot.getValue(User.class);
                // If valid create graphs on page
                if(user != null)
                    createGraphs(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }

    // ---------------------------------------------------------------------
    // Fills in graphs with data from user -- ONLY CALLED IN EVENT LISTENER
    // ---------------------------------------------------------------------
    public void createGraphs(User user){
        // Get all orders
        ArrayList<Order> orders = (ArrayList<Order>)user.getOrdersAsList();

        double totalSum = 0;
        for (Order order : orders) {
            totalSum += order.getTotalSpent();
        }

        // Get Daily, Weekly, Monthly Orders
        ArrayList<Order> dailyOrders = FilterOrders.filterOrderByToday(orders);
        ArrayList<Order> weeklyOrders = FilterOrders.filterOrderByCurrentWeek(orders);
        ArrayList<Order> monthlyOrders = FilterOrders.filterOrderByCurrentMonth(orders);

        // Set up Charts
        initializeDailySpendingBarChart(dailyOrders);
        initializeWeeklySpendingBarChart(weeklyOrders);
        initializeMonthlySpendingBarChart(monthlyOrders);
        initializeCaffeinePieChart(user.getTodayCaffeine());

        // Set Views
        totalCaffeine.setText(String.format(Locale.ENGLISH, "%.2f mg", user.getTodayCaffeine()));
        dailyTotal.setText(String.format(Locale.ENGLISH, "$%.2f", FilterOrders.getOrdersTotal(dailyOrders)));
        weeklyTotal.setText(String.format(Locale.ENGLISH, "$%.2f",  FilterOrders.getOrdersTotal(weeklyOrders)));
        monthlyTotal.setText(String.format(Locale.ENGLISH, "$%.2f",  FilterOrders.getOrdersTotal(monthlyOrders)));
        totalSpendingText.setText("$" + String.format("%.2f", totalSum));
    }

    // ---------------------------------------------------------------------
    // Creating settings for Caffeine Pie Chart
    // ---------------------------------------------------------------------
    public void initializeCaffeinePieChart(double caffeineIntake) {
        // Populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();

        // Add appropriate entries in
        // If caffeine is larger than 400 only add one of caffeine
        int[] colors = new int[] {lightRed};
        pieEntries.add(new PieEntry((float)caffeineIntake , ""));
        if(caffeineIntake < Singleton.dailyCaffeineLimit){
            pieEntries.add(new PieEntry(Singleton.dailyCaffeineLimit - (float)caffeineIntake, ""));
            colors = new int []{lightGreen, gray};
        }

        // Create data set out of pie entries
        PieDataSet dataSet = new PieDataSet(pieEntries, "");

        // Settings of dataSet
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        // Create data out of data set
        // Set Legend and Description to none
        PieData data = new PieData(dataSet);
        caffeinePieChart.getDescription().setEnabled(false);
        caffeinePieChart.getLegend().setEnabled(false);
        caffeinePieChart.setData(data);
        caffeinePieChart.invalidate();
        caffeinePieChart.animate();
        caffeinePieChart.setHoleRadius(80);

    }

    // ---------------------------------------------------------------------
    // Creating settings for Daily Spending Bar Chart
    // ---------------------------------------------------------------------
    public void initializeDailySpendingBarChart(ArrayList<Order> userOrders) {

        // Get Item Mapping & Dec YValues in grpah
        ArrayList<BarEntry> yVals = new ArrayList<>();
        Map<String, Float> itemTotals = FilterOrders.getOrdersItemTotalMap(userOrders);

        // Push everything to two separate arrays
        ArrayList<String> chartItemNames = new ArrayList<>();
        ArrayList<Float> chartItemPrices = new ArrayList<>();
        for(Map.Entry<String, Float> entry : itemTotals.entrySet()) {
            chartItemNames.add(entry.getKey());
            chartItemPrices.add(entry.getValue());
        }
        for(int i = 0; i < chartItemPrices.size(); i++) {
            yVals.add(new BarEntry(i, chartItemPrices.get(i)));
        }

        // Create new set with yValues
        BarDataSet set = new BarDataSet(yVals, "daily total spending");

        // Settings for set of data
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        // Data out of set
        BarData data = new BarData(set);
        data.setBarWidth(0.1f);
        dailySpendingBarChart.setData(data);

        // Format X Axis
        ValueFormatter xAxisFormatter = new IndexAxisValueFormatter(chartItemNames);
        dailySpendingBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        dailySpendingBarChart.getXAxis().setGranularity(1);
        dailySpendingBarChart.getXAxis().setLabelCount(chartItemNames.size());
        dailySpendingBarChart.getXAxis().setValueFormatter(xAxisFormatter);
        dailySpendingBarChart.setFitBars(false);


        // Format Y Axis
        dailySpendingBarChart.getAxisLeft().setGranularity(5);
        dailySpendingBarChart.getAxisLeft().setDrawGridLines(false);
        dailySpendingBarChart.getAxisLeft().setAxisMinimum(0);
        dailySpendingBarChart.getAxisRight().setEnabled(false);

        // Set Graph settings
        dailySpendingBarChart.getDescription().setEnabled(false);
        dailySpendingBarChart.invalidate();
    }

    // ---------------------------------------------------------------------
    // Creating settings for Weekly Spending Bar Chart
    // ---------------------------------------------------------------------
    public void initializeWeeklySpendingBarChart(ArrayList<Order> userOrders) {

        // Get Item Mapping & Dec YValues in grpah
        ArrayList<BarEntry> yVals = new ArrayList<>();
        Map<String, Float> chartMap = FilterOrders.getOrdersDayAmountForWeekMap(userOrders);

        // Push everything to two separate arrays
        ArrayList<String> weekDayNames = new ArrayList<>();
        ArrayList<Float> chartWeekSum = new ArrayList<>();
        for(int i = 0; i < DateFormats.weekDays.length; i++) {
            if(chartMap.containsKey(DateFormats.weekDays[i])) {
                weekDayNames.add(DateFormats.weekDays[i]);
                chartWeekSum.add(chartMap.get(DateFormats.weekDays[i]));
            }
        }
        for(int i = 0; i < chartWeekSum.size(); i++) {
            yVals.add(new BarEntry(i, chartWeekSum.get(i)));
        }

        // Create new set with yValues
        BarDataSet set = new BarDataSet(yVals, "weekly total spending");

        // Settings for set of data
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        // Data out of set
        BarData data = new BarData(set);

        data.setBarWidth(0.1f);
        weeklySpendingBarChart.setData(data);

        // Format X Axis
        ValueFormatter xAxisFormatter = new IndexAxisValueFormatter(weekDayNames);
        weeklySpendingBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        weeklySpendingBarChart.getXAxis().setGranularity(1);
        weeklySpendingBarChart.getXAxis().setLabelCount(weekDayNames.size());
        weeklySpendingBarChart.getXAxis().setValueFormatter(xAxisFormatter);
        weeklySpendingBarChart.setFitBars(false);


        // Format Y Axis
        weeklySpendingBarChart.getAxisLeft().setGranularity(5);
        weeklySpendingBarChart.getAxisLeft().setDrawGridLines(false);
        weeklySpendingBarChart.getAxisLeft().setAxisMinimum(0);
        weeklySpendingBarChart.getAxisRight().setEnabled(false);

        // Set Graph settings
        weeklySpendingBarChart.getDescription().setEnabled(false);
        weeklySpendingBarChart.invalidate();
    }

    // ---------------------------------------------------------------------
    // Creating settings for Weekly Spending Bar Chart
    // ---------------------------------------------------------------------
    public void initializeMonthlySpendingBarChart(ArrayList<Order> userOrders) {

        // Get Item Mapping & Dec YValues in grpah
        ArrayList<BarEntry> yVals = new ArrayList<>();
        Map <String, Float> chartMap = FilterOrders.getOrdersDayAmountForMonthMap(userOrders);

        // Create date list for the month and add values for each date to a separate array
        ArrayList<String> monthDayNames = new ArrayList<>();
        ArrayList<Float> chartMonthDaySum = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        for(int i = 1; i <= 31; i++) {
            String date = DateFormats.monthNames[calendar.get(Calendar.MONTH)] + " " + i;
            if(chartMap.containsKey(date)) {
                monthDayNames.add(date);
                chartMonthDaySum.add(chartMap.get(date));
            }
        }
        for(int i = 0; i < chartMonthDaySum.size(); i++) {
            yVals.add(new BarEntry(i, chartMonthDaySum.get(i)));
        }

        // Create new set with yValues
        BarDataSet set = new BarDataSet(yVals, "monthly total spending");

        // Settings for set of data
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        // Data out of set
        BarData data = new BarData(set);

        // Data Settings
        data.setBarWidth(0.1f);
        monthlySpendingBarChart.setData(data);

        // Format X Axis
        ValueFormatter xAxisFormatter = new IndexAxisValueFormatter(monthDayNames);
        monthlySpendingBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        monthlySpendingBarChart.getXAxis().setGranularity(1);
        monthlySpendingBarChart.getXAxis().setLabelCount(monthDayNames.size());
        monthlySpendingBarChart.getXAxis().setValueFormatter(xAxisFormatter);
        monthlySpendingBarChart.setFitBars(false);


        // Format Y Axis
        monthlySpendingBarChart.getAxisLeft().setGranularity(5);
        monthlySpendingBarChart.getAxisLeft().setDrawGridLines(false);
        monthlySpendingBarChart.getAxisLeft().setAxisMinimum(0);
        monthlySpendingBarChart.getAxisRight().setEnabled(false);

        // Set Graph settings
        monthlySpendingBarChart.getDescription().setEnabled(false);
        monthlySpendingBarChart.invalidate();
    }

}
