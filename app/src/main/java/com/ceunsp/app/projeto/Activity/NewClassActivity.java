package com.ceunsp.app.projeto.Activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class NewClassActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final DatabaseReference ref = firebaseHelper.getReference();
    private final String userID = firebaseHelper.getUserID();
    private String userName, college, course, className, creationDate;
    private static final String PREFERENCES = "Preferences";
    private SharedPreferences preferences;
    private EditText classNameEdit;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        preferences = getSharedPreferences(PREFERENCES, 0 );
        classNameEdit = findViewById(R.id.class_name_edit);


        EditText collegeEdit = findViewById(R.id.college_edit);
        college  = preferences.getString("college", "");
        EditText courseEdit = findViewById(R.id.course_edit);
        course   = preferences.getString("course", "");

        collegeEdit.setText(college);
        courseEdit.setText(course);

        Button saveButton = findViewById(R.id.save_class_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName     = preferences.getString("name", "");
                className    = classNameEdit.getText().toString();
                creationDate = GetCurrentDate();
                CleanUpStrings(college, course);

                if (!className.isEmpty()){

                    DatabaseReference classRef = ref.child("CollegeClass").child(college).child(course);
                    DatabaseReference pushKey = classRef.push();
                    String classID = pushKey.getKey();

                    CollegeClass collegeClass = new CollegeClass(college, course,
                            className, userName, creationDate, classID);
                    pushKey.setValue(collegeClass);

                    DatabaseReference userRef = ref.child("Users").child(userID).child("Student");
                    userRef.child("classID").setValue(classID);
                    saveInPreferences(classID);
                    finish();

                } else {
                    Snackbar.make(v, "Preencha o nome da sala antes de continuar", 20).show();
                }


            }
        });
    }

    public void CleanUpStrings(String str1, String str2){
        str1    = str1.replace(" ", "");
        str2    = str2.replace(" ", "");
        college = str1;
        course  = str2;
    }

    public String GetCurrentDate(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        String date = sdf.format(calendar.getTime());

        return date;
    }

    public void saveInPreferences(String classID){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("classID");
        editor.putString("classID", classID);
        editor.apply();
        editor.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }
}
