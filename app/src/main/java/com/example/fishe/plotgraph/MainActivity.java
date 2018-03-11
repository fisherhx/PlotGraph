package com.example.fishe.plotgraph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void flowG (View view){

        Intent i = new Intent(getApplicationContext(), FlowGraphPlot.class);
        startActivity(i);
    }
    public void weightG (View view){

        Intent i = new Intent(getApplicationContext(), WeightGraphPlot.class);
        startActivity(i);
    }
}
