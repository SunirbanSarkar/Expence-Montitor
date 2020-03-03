package com.sunirban.expensemonitor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sunirban.expensemonitor.Model.Data;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HomeActivity extends AppCompatActivity{


    private Toolbar toolbar;

    private FloatingActionButton fab_btn;
    private FloatingActionButton fab_btn2;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private TextView totalsumresult;


    //global

    private String type;
    private double amount;
    private String note;
    private String post_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Expense Monitor");

        totalsumresult=findViewById(R.id.total_amount);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();

        String uId=mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Expenses").child(uId);

        mDatabase.keepSynced(true);

        recyclerView=findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //Total sum

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double totalamount=0;
                int i=0;
                for (DataSnapshot snap:dataSnapshot.getChildren()){

                    Data data=snap.getValue(Data.class);

                    totalamount+=data.getAmount();

                    String sttotal=String.valueOf(totalamount);

                    totalsumresult.setText(sttotal);

                    i+=1;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab_btn=findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });

        fab_btn2=findViewById(R.id.fab2);
        fab_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),GraphActivity.class));
            }
        });

    }


    private void customDialog(){


        AlertDialog.Builder mydialog= new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater= LayoutInflater.from(HomeActivity.this);
        View myview=inflater.inflate(R.layout.input_data,null);

        final AlertDialog dialog=mydialog.create();

        dialog.setView(myview);

      /*  spinner=findViewById(R.id.edt_type);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);*/

        final EditText type=myview.findViewById(R.id.edt_type);
        final EditText amount=myview.findViewById(R.id.edt_amount);
        final EditText note=myview.findViewById(R.id.edt_note);
        Button btnSave=myview.findViewById(R.id.btn_save);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mType=type.getText().toString().trim();
                String mAmount=amount.getText().toString().trim();
                String mNote=note.getText().toString().trim();

                double ammint=Double.parseDouble(mAmount);

                if(TextUtils.isEmpty(mType)){
                    type.setError("Required Field..");
                    return;
                }

                if(TextUtils.isEmpty(mAmount)){
                    amount.setError("Required Field..");
                    return;
                }

                if(TextUtils.isEmpty(mNote)){
                    note.setError("Required Field..");
                    return;
                }

                String id=mDatabase.push().getKey();

                String date= DateFormat.getDateInstance().format(new Date());

                Data data= new Data(mType,ammint,mNote,date,id);

                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(),"Data Added",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

  /*  public void customDialog2(){

        AlertDialog.Builder mydialog= new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater= LayoutInflater.from(HomeActivity.this);
        View myview=inflater.inflate(R.layout.graph_view,null);

        final AlertDialog dialog=mydialog.create();

        dialog.setView(myview);

        mChart=findViewById(R.id.line_chart);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

       // yValues=new ArrayList<>();
        LineDataSet set1= new LineDataSet(yValues,"Data set 1");

        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(set1);

        LineData data=new LineData(dataSets);

        mChart.setData(data);

        Button btnBack=myview.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }*/

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {

                viewHolder.setDate(model.getDate());
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setAmmount(model.getAmount());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(position).getKey();
                        type=model.getType();
                        amount=model.getAmount();
                        note=model.getNote();

                        updateData();

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
        }

        public void setType(String type){

            TextView mType=myview.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setNote(String note){

            TextView mNote=myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){

            TextView mDate=myview.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setAmmount(double amount){

            TextView mAmount=myview.findViewById(R.id.amount);

            String stam=String.valueOf(amount);
            mAmount.setText(stam);
        }

    }

    public void updateData(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);

        View mView=inflater.inflate(R.layout.update_inputfield,null);

        final AlertDialog dialog=mydialog.create();

        dialog.setView(mView);

        final EditText edt_Type=mView.findViewById(R.id.edt_type_upd);
        final EditText edt_Amount=mView.findViewById(R.id.edt_amount_upd);
        final EditText edt_Note=mView.findViewById(R.id.edt_note_upd);

        edt_Type.setText(type);
        edt_Type.setSelection(type.length());

        edt_Amount.setText(String.valueOf(amount));
        edt_Amount.setSelection(String.valueOf(amount).length());

        edt_Note.setText(note);
        edt_Note.setSelection(note.length());

        Button btnUpdate=mView.findViewById(R.id.btn_save_upd);
        Button btnDelete=mView.findViewById(R.id.btn_delete_upd);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edt_Type.getText().toString().trim();
                String mAmount=String.valueOf(amount);
                mAmount=edt_Amount.getText().toString().trim();
                note=edt_Note.getText().toString().trim();

                double damount=Double.parseDouble(mAmount);

                String date=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(type,damount,note,date,post_key);

                mDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
