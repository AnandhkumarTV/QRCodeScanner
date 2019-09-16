package tv.anandhkumar.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan,buttonAdd,buttonGetData,buttonViewAll;
    private TextView textViewName, textViewAddress;
    private EditText resultText;

    //qr code scanner object
    private IntentIntegrator qrScan;

    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View objects
        buttonScan = findViewById(R.id.buttonScan);
        textViewName =  findViewById(R.id.textViewName);
        textViewAddress =  findViewById(R.id.textViewAddress);

        myDB = new DatabaseHelper(this);

        buttonAdd = findViewById(R.id.button_add);
        buttonGetData = findViewById(R.id.button_view);
        buttonViewAll = findViewById(R.id.button_viewAll);

        resultText = findViewById(R.id.result);

        AddData();

        getData();

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
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                   // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    resultText.setText(result.getContents());
                }
            }
        } else {
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
                Toast.makeText(MainActivity.this, "Data : "+buffer.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {
        buttonGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = resultText.getText().toString();

                if (id.equals(String.valueOf(""))){
                    resultText.setError("Scan again");
                    return;
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

            }
        });
    }

    public void AddData(){
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = myDB.insertData(
                        resultText.getText().toString());

                if (isInserted == true){
                    Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}
