package com.ceunsp.app.projeto.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.ceunsp.app.projeto.Helpers.ClassAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.UsersAdapter;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.Model.Students;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JoinClassActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final DatabaseReference ref = firebaseHelper.getReference();
    private final String userId = firebaseHelper.getUserID();
    private List<User> studentsList = new ArrayList<User>();
    private static final String PREFERENCES = "Preferences";
    private SharedPreferences preferences;
    private String userType, classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()){
            retrieveClassID(bundle);
        }


        preferences = getSharedPreferences(PREFERENCES, 0);
        userType = preferences.getString("userType", "");

        if (userType.equals("Aluno")){

            String college = cleanUpStrings(preferences.getString("college", ""));
            String course  = cleanUpStrings(preferences.getString("course", ""));

            Query studentsQry = ref.child("Users").orderByChild("name").equalTo(classID);
            /*DatabaseReference classStudentsRef = ref.child("CollegeClass")
                    .child(college).child(course).child(classID).child("students");

            classStudentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            retrieveStudents(postSnapshot);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });*/
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userType.equals("Aluno")){
                    DatabaseReference userRef = ref.child("Users").child(firebaseHelper.getUserID());
                    userRef.child("colleClassID").setValue(classID);
                    saveInPreferences();
                    finish();
                }
            }
        });
    }
    public void retrieveClassID(Bundle bundle){
        classID = bundle.getString("classID");
    }

    public String cleanUpStrings(String str) {
        str = str.replace(" ", "");
        return str;
    }

    public void fillRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.user_RecyclerView);
        UsersAdapter adapter = new UsersAdapter(studentsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JoinClassActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void retrieveStudents(DataSnapshot postSnapshot){

        String userKey = (String)postSnapshot.getValue();
        Query userQry = ref.child("Users").child(userKey).orderByChild("name");

        userQry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (int index = 0; index >= dataSnapshot.getChildrenCount(); index++){

                        String name     = (String) dataSnapshot.child("name").getValue();
                        String lastName = (String) dataSnapshot.child("lastName").getValue();
                        String userType = (String) dataSnapshot.child("userType").getValue();
                        User user = new User(name, lastName, userType);
                        studentsList.add(user);

                        fillRecyclerView();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void saveInPreferences(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("collegeClassID", classID);
        editor.apply();
        editor.commit();
    }
}
