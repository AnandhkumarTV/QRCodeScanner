package tv.anandhkumar.quotesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText userNameStory;
    Button btnsaveusername;

    DatabaseReference reference;

    String USER_NAME_STORY = "usernamestory";
    String userNameStoryLocal = "";
    String userNameStoryNew = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadDataUsernameLocal();
        checkUsernameLocal();

        userNameStory = findViewById(R.id.userNameStory);
        btnsaveusername = findViewById(R.id.btnsaveusername);

        btnsaveusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(userNameStory.getText().toString());

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //save to firebase
                        reference.getRef().child("username").setValue(userNameStory.getText().toString());

                        // save username to the local storage to keep as a key whenever we get the data from Firebase
                        SharedPreferences sharedPreferences = getSharedPreferences(USER_NAME_STORY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(userNameStoryLocal, userNameStory.getText().toString());
                        editor.apply();

                        // give a notice
                        Toast.makeText(getApplicationContext(), "Ok Done", Toast.LENGTH_SHORT).show();

                        // pass into another activity
                        Intent a = new Intent(MainActivity.this,AddPhotoAct.class);
                        startActivity(a);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void loadDataUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_NAME_STORY, MODE_PRIVATE);
        userNameStoryNew = sharedPreferences.getString(userNameStoryLocal, "");
    }

    public void checkUsernameLocal(){
        if(userNameStoryNew.isEmpty()){
            // if the username is not available
        }
        else {
            Intent a = new Intent(MainActivity.this,AddPhotoAct.class);
            startActivity(a);
        }
    }
}


