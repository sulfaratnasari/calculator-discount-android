package com.example.discountcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {
    DatabaseHelper mydb;
    Button btnSave, calculate, btnViewAll, btnDeleteAll;
    Switch switchTax, switchDisc2;
    EditText harga, discount1,discount2, editTax;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DatabaseHelper(this);
        final EditText harga = (EditText) findViewById(R.id.editText2);
        final EditText discount1 = (EditText) findViewById(R.id.editText);
        final TextView result = (TextView) findViewById(R.id.textView3);
        final EditText editTax = (EditText) findViewById(R.id.editTax);
        final EditText discount2 = (EditText) findViewById(R.id.editText3);
        calculate = (Button) findViewById(R.id.button);
        btnSave = (Button) findViewById(R.id.buttonSave);
        btnViewAll = (Button) findViewById(R.id.getAll);
        btnDeleteAll = (Button) findViewById(R.id.buttonDelete);
        switchTax = (Switch) findViewById(R.id.switch1);
        switchDisc2 = (Switch) findViewById(R.id.switchDisc2);
        switchDisc2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if(switchDisc2.isChecked()){
                  discount2.setVisibility(View.VISIBLE);
              }else{
                  discount2.setVisibility(View.INVISIBLE);
              }
          }
      });

        switchTax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchTax.isChecked()){
                    editTax.setVisibility(View.VISIBLE);
                }else{
                    editTax.setVisibility(View.INVISIBLE);
                }
            }
        });

        String strPrice = harga.getText().toString();
        if(TextUtils.isEmpty(strPrice)){
            harga.setError("Price cannot be empty");
        }
        String strdisc1 = discount1.getText().toString();
        if(TextUtils.isEmpty(strdisc1)){
            discount1.setError("Discount 1 cannot be empty");
        }
        calculate.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (harga.getText().toString().isEmpty()||discount1.getText().toString().isEmpty()) {
                                    showMsg("Error", "Original Price or Discount 1 can not be empty");
                                } else{
                                    double harga1 = Double.parseDouble(harga.getText().toString());
                                double disc = Double.parseDouble(discount1.getText().toString());
                                double disc2 = 0.0, total2 = 0.0, totalFinal = 0.0;
                                double total = harga1 - (harga1 * disc / 100);
                                totalFinal = total;
                                if (switchDisc2.isChecked()) {
                                    disc2 = Double.parseDouble(discount2.getText().toString());
                                    total2 = total - (total * disc2 / 100);
                                    totalFinal = total2;
                                }
                                double extraSaving = 0.0;
                                if (switchTax.isChecked()) {
                                    double tax = Double.parseDouble(editTax.getText().toString());
                                    double priceTax = totalFinal * tax / 100;
                                    totalFinal += priceTax;
                                    extraSaving = tax / 100 * harga1;
                                }
                                double saving = harga1 - totalFinal + extraSaving;
                                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                                String totalFinalString = numberFormat.format(round(totalFinal, 2));
                                String savingString = numberFormat.format(round(saving, 2));
                                result.setText("Price After Discount: " + totalFinalString + "\n" + "Savings : " + savingString);
                            }
                            }
                        }
                );
        btnSave.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        double harga1 = Double.parseDouble(harga.getText().toString());
                        double disc = Double.parseDouble(discount1.getText().toString());
                        double disc2 = 0.0, total2 = 0.0, totalFinal = 0.0;
                        double total = harga1 - (harga1 * disc / 100);
                        totalFinal = total;
                        if (switchDisc2.isChecked()) {
                            disc2 = Double.parseDouble(discount2.getText().toString());
                            total2 = total - (total * disc2 / 100);
                            totalFinal = total2;
                        }
                        double extraSaving = 0.0, priceTax = 0.0, tax =0.0;
                        if(switchTax.isChecked()){
                            tax = Double.parseDouble(editTax.getText().toString());
                            priceTax = totalFinal * tax/100;
                            totalFinal +=priceTax;
                            extraSaving = tax/100 * harga1;
                        }
                        double saving = harga1 - totalFinal + extraSaving;
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        String totalFinalString = numberFormat.format(round(totalFinal,2));
                        String savingString = numberFormat.format(round(saving,2));
                        String harga1String = numberFormat.format(harga1);
                        String priceTaxString = numberFormat.format(priceTax);
                        boolean isInserted = mydb.insertStuff(totalFinalString, savingString, harga1String, priceTaxString, tax, disc, disc2);
                        if(isInserted=true)
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Not Saved", Toast.LENGTH_LONG).show();
                    }
                }
        );
        btnViewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Cursor res = mydb.getData();
                       if(res.getCount()==0){
                           showMsg("", "No data found");
                           return;
                       }
                       StringBuffer buffer = new StringBuffer();
                       while(res.moveToNext()){
                           buffer.append("Id : " + res.getInt(0) + "\n");
                           buffer.append("Original price : " + res.getString(3) + "\n");
                           buffer.append("Tax : " + res.getDouble(5) + "\n");
                           buffer.append("Disc1 : " + res.getDouble(6) + "\n");
                           buffer.append("Disc2 : " + res.getDouble(7) + "\n");
                           buffer.append("Saving : " + res.getString(2) + "\n");
                           buffer.append("Price after discount : " + res.getString(1) +"\n" + "\n");
                       }
                       showMsg("Data", buffer.toString());
                    }
                }
        );
        btnDeleteAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer rowDeleted = mydb.deleteAll();
                        if(rowDeleted > 0)
                        Toast.makeText(MainActivity.this, "Deleted" , Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void showMsg(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
