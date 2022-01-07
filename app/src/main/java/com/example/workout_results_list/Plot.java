package com.example.workout_results_list;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
    private String[][] subjMatrix;
    Object[] vector;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

//        subjMatrix = Arrays.copyOf(vector, vector.length, String[][].class);
        vector = (Object[])getIntent().getSerializableExtra("exerData");
        subjMatrix = Arrays.copyOf(vector, vector.length, String[][].class);

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
        barChart.animateXY(500, 500);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void graphInitSetting(){
        ArrayList<Integer> exer_data = new ArrayList<>();

        labelList.add("오 늘");
        labelList.add("하루전");
        labelList.add("이틀전");

//        jsonList.add(Integer.valueOf(prevExerTotal));
//        jsonList.add(Integer.valueOf(prevExerTotal2));
//        jsonList.add(Integer.valueOf(prevExerTotal3));

        BarChartGraph(labelList, jsonList);
        barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
        //barChart.setRendererLeftYAxis();
        barChart.setMaxVisibleValueCount(50);
        barChart.setAutoScaleMinMaxEnabled(false);
        barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
        barChart.getAxisLeft().setAxisMaxValue(500);
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
