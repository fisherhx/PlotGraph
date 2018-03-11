package com.example.fishe.plotgraph;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeightGraphPlot extends AppCompatActivity {
    
    SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss");
    Double maxWeight1=Double.MIN_VALUE;
    Double maxWeight2=Double.MIN_VALUE;
    Double maxWeight3=Double.MIN_VALUE;
    String maxTime1;
    String maxTime2;
    String maxTime3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_graph_plot);
        InputStream inputStream = getResources().openRawResource(R.raw.tck);
        CSVFile csvFile = new CSVFile(inputStream);
        List<String[]> dataList = csvFile.read();
        ArrayList<TCKData> tck= new ArrayList<>();
        LineGraphSeries<DataPoint> series;

        TextView max1 = (TextView) findViewById(R.id.textView1);
        TextView max2 = (TextView) findViewById(R.id.textView2);
        TextView max3 = (TextView) findViewById(R.id.textView3);

        int numTime = 0;
        String[] firstData = dataList.get(0);
        String prevTime = firstData[0].substring(11,19);
        double weight = 0.0;
        double totalWeight = 0.0;
        double totalFlow = 0.0;
        double avgWeight = 0.0;
        double avgFlow = 0.0;

        for (int i = 0; i < dataList.size(); i++){
            String[] data = dataList.get(i);
            String currTime;
            try{
                currTime = data[0].substring(11,19);
                double flowRate = Double.parseDouble(data[1]);
                double currWeight = Double.parseDouble(data[2]);

                if(prevTime.equals(currTime)){
                    totalWeight += currWeight;
                    totalFlow += flowRate;
                    numTime ++;
                }
                else if((dataList.size()-1) == i){
                    avgFlow = (totalFlow + flowRate)/numTime;
                    avgWeight = (totalWeight + currWeight)/numTime;
                    TCKData tckData = new TCKData();
                    tckData.setFlowRate(avgFlow);
                    tckData.setWeight(avgWeight);
                    tckData.setDate(data[0].substring(11,19));
                    tck.add(tckData);
                }
                else{
                    avgFlow = totalFlow/numTime;
                    avgWeight = totalWeight/numTime;
                    totalFlow = flowRate;
                    totalWeight = currWeight;
                    numTime = 1;
                    TCKData tckData = new TCKData();
                    tckData.setFlowRate(avgFlow);
                    tckData.setWeight(avgWeight);
                    tckData.setDate(prevTime);
                    tck.add(tckData);
                    //Log.i("Curr Date", String.valueOf(data[0].substring(14,19)));
                    //Log.i("Taking Final weight", String.valueOf(avgWeight));
                }
                prevTime = currTime;

            } catch (NumberFormatException e) {
                continue;
            }
        }

        GraphView graph = findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        for(int i =0; i<tck.size(); i++) {
            TCKData tckData = new TCKData();
            tckData = tck.get(i);
            Date time = new Date();
            try {
                time = dataFormat.parse(tckData.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            weight = tckData.getWeight();

            //Obtain the 3 peaks
            if (weight > maxWeight1){
                maxWeight3 = maxWeight2;
                maxTime3 = maxTime2;
                maxWeight2 = maxWeight1;
                maxTime2 = maxTime1;
                maxWeight1 = weight;
                maxTime1 = tckData.getDate();
            }
            else if (weight > maxWeight2) {
                maxWeight3 = maxWeight2;
                maxTime3 = maxTime2;
                maxWeight2 = weight;
                maxTime2 = tckData.getDate();
            }
            else if (weight > maxWeight3) {
                maxWeight3 = weight;
                maxTime3 = tckData.getDate();
            }
            series.appendData(new DataPoint(time, weight), true, 100);
        }

        DecimalFormat df = new DecimalFormat("#.##");

        max1.setText("Peak 1 = "+df.format(maxWeight1).toString() + "\nAt time: " + maxTime1);
        max2.setText("Peak 2 = "+df.format(maxWeight2).toString() + "\nAt time: " + maxTime2);
        max3.setText("Peak 3 = "+df.format(maxWeight3).toString() + "\nAt time: " + maxTime3);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0.0);
        graph.getViewport().setMaxY(200.0);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            public String formatLabel(double value, boolean isValueX){
                if(isValueX){
                    return dataFormat.format(new Date((long)value));
                }
                else{
                    return super.formatLabel(value,isValueX);
                }
            }
        });

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Weight");
        gridLabel.setVerticalAxisTitleColor(Color.BLUE);
        gridLabel.setHorizontalAxisTitleColor(Color.BLUE);
        graph.addSeries(series);

        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(false);

    }

}
