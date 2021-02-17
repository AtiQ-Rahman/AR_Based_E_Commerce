package com.augmented_reality.AR_Based_E_Commerce.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.augmented_reality.AR_Based_E_Commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditOrder extends AppCompatActivity {
    EditText productName,productPrice,productColor;
    Button btn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        order_id = getIntent().getStringExtra("order_id");
        setContentView(R.layout.activity_edit_order);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productColor = findViewById(R.id.productColor);
        btn = findViewById(R.id.update);
        get_user();

    }

    public void get_user(){

        db.collection("Orders").document(order_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isComplete()){
                    Toast.makeText(getApplicationContext(),"Failded1",Toast.LENGTH_LONG).show();
                    DocumentSnapshot documentSnapshot=task.getResult();
                    Map<String,Object> data=documentSnapshot.getData();
                    productName.setText(data.get("product_name").toString());
                    productPrice.setText(data.get("product_price").toString());
                    productColor.setText(data.get("product_color").toString());
                   // Toast.makeText(getApplicationContext(),"color "+data.get("product_color").toString(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void update_order(View view){
        String product_name = productName.getText().toString();
        String product_price = productPrice.getText().toString();
        String product_color = productColor.getText().toString();

//        Pattern psNum = Pattern.compile("/^(?:\\+88|88)?(01[3-9]\\d{9})$/");
//        Matcher msNum = psNum.matcher(user_email);
//        boolean bsNum = msNum.matches();
        //Toast.makeText(getApplicationContext(),"User Nmae"+userName.getText().toString()+"email:"+email.getText().toString(),Toast.LENGTH_LONG).show();

        Map<String,Object> data=new HashMap<>();
        data.put("product_name",product_name);
        data.put("product_price",product_price);
        data.put("product_color",product_color);
        DocumentReference documentReference=db.collection("Orders").document();


        db.collection("Orders").document(order_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent tnt=new Intent(getApplicationContext(),AllOrdersForAdmin.class);
                startActivity(tnt);
                Toast.makeText(getApplicationContext(),"Completed",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failded",Toast.LENGTH_LONG).show();
            }
        });
    }
}