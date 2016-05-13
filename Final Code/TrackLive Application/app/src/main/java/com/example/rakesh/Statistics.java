package com.example.rakesh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        PieChart pieChart = (PieChart) findViewById(R.id.chart);
// creating data values
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1f, 0));
        entries.add(new Entry(1f, 1));
        entries.add(new Entry(28f, 2));
        entries.add(new Entry(6f, 3));

        // entries.add(new Entry(9f, 5));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        // creating labels
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("ClimbingUP");
        labels.add("ClimbingDown");
        labels.add("Sitting");
        labels.add("Walking");


        PieData data = new PieData(labels, dataset); // initialize Piedata
        pieChart.setData(data); //set data into chart
        pieChart.setDescription("Description");  // set the description
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); // set the color
        pieChart.animateY(5000);

    }
}
