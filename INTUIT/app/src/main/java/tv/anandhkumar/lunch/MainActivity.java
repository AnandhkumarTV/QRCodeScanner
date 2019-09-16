package tv.anandhkumar.lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonScan,buttonViewAll;
    TextView resultText;

    //qr code scanner object
    private IntentIntegrator qrScan;

    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);

        buttonScan = findViewById(R.id.scan);
        resultText = findViewById(R.id.resultText);
        buttonViewAll = findViewById(R.id.button_viewAll);

        AddData();

        viewAll();


        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qr code has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                    // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    resultText.setText(result.getContents());

                  /*  if (result.equals(getData())){
                        Toast.makeText(this, "Already visited", Toast.LENGTH_SHORT).show();
                    }else {
                        AddData();
                    }*/
                  AddData();
                }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void viewAll() {
        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = myDB.getAllData();

                if (cursor.getCount() == 0){
                    Toast.makeText(MainActivity.this, "Sorry, No records found", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuffer buffer = new StringBuffer();

                while (cursor.moveToNext()){
                    buffer.append("ID: "+cursor.getString(0)+"\n");

                }
                Toast.makeText(MainActivity.this, ""+buffer.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
/*
    private String getData() {
        String id = resultText.getText().toString();

        if (id.equals(String.valueOf(""))){
            Toast.makeText(this, "Scan again", Toast.LENGTH_SHORT).show();

        }
        Cursor cursor = myDB.getData(id);
        String data = null;

        if (cursor.getCount()==0){
            Toast.makeText(MainActivity.this, "Sorry, No records found", Toast.LENGTH_SHORT).show();
        }

        if (cursor.moveToNext()){
            data = "ID: "+cursor.getString(0)+"\n";

            Toast.makeText(MainActivity.this, "Data : "+data, Toast.LENGTH_SHORT).show();
        }
        return data;
    }

 */

    public void AddData(){
        boolean isInserted = myDB.insertData(
                resultText.getText().toString());

        if (isInserted == true){
            Toast.makeText(getApplicationContext(),"Had your lunch :-)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Already Visited :-(",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
