package com.example.workout_results_list;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Plot extends Activity {
    Date date = new Date();
    String strDate;
    String strDate2;
    String[] phpReturnData;
    Button button_time;
    Button button_number;

    BarChart barChart;
    ArrayList<Integer> jsonList = new ArrayList<>(); // ArrayList 선언
    ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언

    private String[] exerDate;
    private String[] exerDay;
    private String id;
    private String exer1;
    private int data1;
    private String[][] exerMatrix;
    Object[] vector;


    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        vector = (Object[])getIntent().getSerializableExtra("exerData");
        exerMatrix = Arrays.copyOf(vector, vector.length, String[][].class);

        //BarChart
        barChart = (BarChart) findViewById(R.id.chart1);
        graphInitSetting();       //그래프 기본 세팅
    }

    /**
     * 그래프함수
     */
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {
        // BarChart 메소드

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry(i, (Integer) valList.get(i)));
        }

        BarDataSet depenses = new BarDataSet(entries, "일일 사용시간"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
//        barChart.setDescription(" ");
//        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//        dataSets.add((IBarDataSet) depenses);
        BarData data = new BarData (depenses);
//        data.setBarWidth(1f);

        barChart.getAxisRight().setEnabled(false);
        depenses.setColors(ColorTemplate.JOYFUL_COLORS);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setTextSize(17);
        barChart.getXAxis().setTextColor(Color.GRAY);
        barChart.getXAxis().setLabelCount(3);
        barChart.getXAxis().setCenterAxisLabels(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelList));

        barChart.setData(data);
        barChart.setVisibleXRangeMaximum(5);
        barChart.moveViewToX(1);

        barChart.animateXY(500, 500);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void graphInitSetting(){
        ArrayList<Integer> exer_data = new ArrayList<>();
        ArrayList<String> exer_day = new ArrayList<>();

        int i = 0;
        while(i < exerMatrix.length) {
            exer_data.add(Integer.valueOf(exerMatrix[i][2]) + Integer.valueOf(exerMatrix[i][3]) + Integer.valueOf(exerMatrix[i][4]) + Integer.valueOf(exerMatrix[i][5]));
            exer_day.add(exerMatrix[i][1]);
            i++;
        }

        int data_length;

        ParsePosition pp1 = new ParsePosition(0);
        ParsePosition pp2 = new ParsePosition(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat_simple = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date1 = dateFormat.parse (exer_day.get(0), pp1);
        Date date2 = dateFormat.parse (exer_day.get(i-1), pp2);

        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        data_length = (int) (date2.getTime() - date1.getTime())/ (24 * 60 * 60 * 1000);

        int k = 0; // k: exerData index, j: exerDay index
        for (int j = 0; j < data_length; j++) {
            labelList.add(dateFormat_simple.format(c.getTime()));

            if(dateFormat.format(c.getTime()).equals(exer_day.get(k)))
            {
                jsonList.add(Integer.valueOf(exer_data.get(k)));
                k++;
            }
            else
                jsonList.add(0);

            c.add(Calendar.DATE, 1);
        }


        BarChartGraph(labelList, jsonList);
        barChart.setTouchEnabled(true); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
        //barChart.setRendererLeftYAxis();
//        barChart.setMaxVisibleValueCount(50);
        barChart.setAutoScaleMinMaxEnabled(true);
//        barChart.getAxisLeft().setAxisMaxValue(500);
        LimitLine ll1;
        ll1 = new LimitLine(200f, "목표 수치");

        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(20f);
//        ll1.setTypeface(tf);

        barChart.getAxisLeft().addLimitLine(ll1);
//        barChart.getXAxis().setAxisMaximum((float) 30);
    }

    public void onBackPressed() {
        finish();
    }
}
