package com.augmented_reality.AR_Based_E_Commerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//seller=admin
//buyer=admin
public class SelectUserType extends AppCompatActivity {

    String user_type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);
    }
    public void admin(View view){
        user_type="admin";
        Intent tnt=new Intent(getApplicationContext(),SignIn.class);
        tnt.putExtra("user_type",user_type);
        startActivity(tnt);
        finish();
    }
    public void customer(View view){
        user_type="customer";
        Intent tnt=new Intent(getApplicationContext(),SignIn.class);
        tnt.putExtra("user_type",user_type);
        startActivity(tnt);
        finish();
    }
}
