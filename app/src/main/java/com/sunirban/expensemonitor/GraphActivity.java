package com.sunirban.expensemonitor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sunirban.expensemonitor.Model.Data;

public class GraphActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    GraphView graphView;
    LineGraphSeries<DataPoint> series;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();

        String uId=mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Expenses").child(uId);

        graphView=findViewById(R.id.line_chart);
        series=new LineGraphSeries<DataPoint>();
        graphView.addSeries(series);



        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return "Entry"+super.formatLabel(value, isValueX);
                }
                else {
                    return "Rs"+super.formatLabel(value, isValueX);
                }
            }
        });

        Button btnBack=findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataPoint[] dp=new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index=0;

                for(DataSnapshot myDataSnapshot: dataSnapshot.getChildren()){
                    Data data=myDataSnapshot.getValue(Data.class);
                    dp[index]=new DataPoint(Double.valueOf(index),data.getAmount());
                    index+=1;
                }
                series.resetData(dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
