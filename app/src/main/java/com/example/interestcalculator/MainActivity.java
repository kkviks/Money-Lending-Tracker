package com.example.interestcalculator;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private Button btn_start_date, btn_end_date,btn_calc;
    private TextView txtNumDays,txtPrincipalAmount;
    private double RATE_PER_MONTH = 2.0;
    private boolean startOrEnd = true;
    private int numDays=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogFragment datePicker = new DatePickerFragment();

        btn_start_date = findViewById(R.id.startDate);
        btn_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(),"date picker");
                startOrEnd = true ;
            }
        });

        btn_end_date  = findViewById(R.id.endDate);
        btn_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(),"date picker");
                startOrEnd = false ;
            }
        });

        txtNumDays = findViewById(R.id.textNumDays);

        txtPrincipalAmount = findViewById(R.id.txtPrincipalAmount);

        TextView output = findViewById(R.id.res);
        output.setText("Hiiiiiiiiii!");

        btn_calc = findViewById(R.id.btn_calc);
        btn_calc.setOnClickListener(new View.OnClickListener() {

            @SuppressLint({"SetTextI18n", "ShowToast"})
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String startDate = btn_start_date.getText().toString();
                String endDate = btn_end_date.getText().toString();

                if(startDate.equals("FROM") || endDate.equals("TO")){
                    return;
                }

               try{
                   //Changing from from 02-Jun-2021 to yyyy-MM-dd
                   @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd-MMM-yyyy");
                   @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfmt2 = new SimpleDateFormat("yyyy-MM-dd");

                   startDate = sdfmt2.format(sdfmt1.parse(startDate));
                   endDate = sdfmt2.format(sdfmt1.parse(endDate));

                   //Parsing the date
                   LocalDate dateBefore = LocalDate.parse(startDate);
                   LocalDate dateAfter = LocalDate.parse(endDate);

                   //calculating number of days in between
                   numDays = (int) ChronoUnit.DAYS.between(dateBefore,dateAfter);
                   numDays = Math.abs(numDays);
                   txtNumDays.setText(String.valueOf(numDays)+ " days");
                   txtNumDays.setVisibility(View.VISIBLE);

                   //Rending table
                   try{
                       double p = Double.parseDouble(txtPrincipalAmount.getText().toString());
                       int days = numDays;

                       renderTable(p,days);

                   }catch (Exception e){
                       Toast.makeText(getApplicationContext(),"Render Error!",Toast.LENGTH_SHORT);
                   }

                   //Toast.makeText(getApplicationContext(),String.valueOf(numDays),Toast.LENGTH_SHORT).show();

               }catch (Exception e){
                   Toast.makeText(getApplicationContext(),"Parse Error!",Toast.LENGTH_SHORT);
               }
            }
        });
    }

    @SuppressLint("ResourceType")
    private void renderTable(double p, int curr_days) {
        //https://stackoverflow.com/questions/24078275/how-to-add-a-row-dynamically-in-a-tablelayout-in-android


        int row_count = (int) Math.ceil((double)curr_days/365.0);

        int i = 1;

        row_count+=2; //For safe id naming
        String ans = "(Year, Days, Interest, Principal, Total):\n\n";

        double itr, pr_year,tot;

        while(curr_days>0){
            ans += String.valueOf(i++);
            if(curr_days>=365){
                itr = p*(RATE_PER_MONTH/30.42)*(365)/(double)100.0;
                pr_year = p; tot = pr_year+itr;
                p = tot;
                curr_days-=365;
                ans += " 365 ";
            }else{
                pr_year = p;
                itr = p*(RATE_PER_MONTH/30.42)*(curr_days)/100.0;
                tot = pr_year+itr;
                p = tot;
                ans+= String.valueOf(curr_days);
                curr_days = -1;
            }
            ans += String.valueOf((int)(itr)) + " ";
            ans += String.valueOf((int)(pr_year))+" ";
            ans += String.valueOf((int)(tot)) + "\n\n";
        }
        TextView output = findViewById(R.id.res);
        output.setVisibility(View.VISIBLE);
        output.setText(ans);
        Toast.makeText(getApplicationContext(),ans,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance().format(c.getTime());

        if(startOrEnd){
            btn_start_date.setText(currentDateString);
        }else{
            btn_end_date.setText(currentDateString);
        }
    }
}