package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.augmented_reality.AR_Based_E_Commerce.R;
import com.augmented_reality.AR_Based_E_Commerce.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sslcommerz.library.payment.model.datafield.MandatoryFieldModel;
import com.sslcommerz.library.payment.model.dataset.TransactionInfo;
import com.sslcommerz.library.payment.model.util.CurrencyType;
import com.sslcommerz.library.payment.model.util.ErrorKeys;
import com.sslcommerz.library.payment.model.util.SdkCategory;
import com.sslcommerz.library.payment.model.util.SdkType;
import com.sslcommerz.library.payment.viewmodel.listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.viewmodel.management.PayUsingSSLCommerz;

import java.util.HashMap;
import java.util.Map;

public class Payment extends AppCompatActivity  {

    public String TAG="Main";
    TextView paymenTAmount;
    String amount="";
    int randomNum = 100000 + (int)(Math.random() * 999999);
    String transactionID;
    int price;
    public String product_id="",user_id="",product_name="",compress_image_path="",color="",type="";

    ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        paymenTAmount=findViewById(R.id.amount);
        Intent intent = getIntent();
        //intent.getStringExtra("price")
        // ////////////////////////////////////////////////////////////////////////////////////////////////////////
        price=intent.getIntExtra("price",0);
        product_id = intent.getStringExtra("product_id");
        user_id= SharedPrefManager.getInstance(getApplicationContext()).getUser().user_id;
        product_name = intent.getStringExtra("product_name");
        compress_image_path = intent.getStringExtra("compress_image_path");
        color = intent.getStringExtra("product_color");
        type = intent.getStringExtra("product_type");
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        paymenTAmount.setText(price+"");
        //Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void sendPayment(View view){

        transactionID=String.valueOf(randomNum);
        MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel("htech5fd7c932ecc06", "htech5fd7c932ecc06@ssl", price+"", transactionID, CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_LIST);

        PayUsingSSLCommerz.getInstance().setData(Payment.this, mandatoryFieldModel, new OnPaymentResultListener() {
            @Override
            public void transactionSuccess(TransactionInfo transactionInfo) {

                if (transactionInfo.getRiskLevel().equals("0")) {

                    Toast.makeText(getApplicationContext(),"Transaction succeeded",Toast.LENGTH_LONG).show();
                    addnewOrder();

                }

                else {
                    Log.e(TAG, "Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle());
                    Toast.makeText(getApplicationContext(),"Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle(),Toast.LENGTH_LONG).show();
                }
            }

            public void addnewOrder() {
                Map<String,Object> data=new HashMap<>();


                DocumentReference documentReference=db.collection("Orders").document();
                String order_id= documentReference.getId();

                data.put("order_id",order_id);
                data.put("customer_id",user_id);
                data.put("product_id",product_id);
                data.put("product_name",product_name);
                data.put("product_price",price);
                data.put("product_color",color);
                data.put("product_type",type);
                data.put("img_path",compress_image_path);
                data.put("order_time", FieldValue.serverTimestamp());




                db.collection("Orders")
                        .document(order_id)
                        .set(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Completed",Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(getApplicationContext(), CustomerDashboard.class);
                                startActivity(intent);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void transactionFail(String s) {
                Log.e(TAG, s);
            }


            @Override
            public void error(int errorCode) {
                switch (errorCode) {
// Your provides inf
                    case ErrorKeys.USER_INPUT_ERROR:
                        Log.e(TAG, "User Input Error");
                        Toast.makeText(getApplicationContext(),"User Input Error",Toast.LENGTH_LONG).show();
                        break;
// Internet is not connected.
                    case ErrorKeys.INTERNET_CONNECTION_ERROR:
                        Log.e(TAG, "Internet Connection Error");
                        break;
// Server is not giving valid data.
                    case ErrorKeys.DATA_PARSING_ERROR:
                        Log.e(TAG, "Data Parsing Error");
                        break;
// User press back button or canceled the transaction.
                    case ErrorKeys.CANCEL_TRANSACTION_ERROR:
                        Log.e(TAG, "User Cancel The Transaction");
                        break;
// Server is not responding.
                    case ErrorKeys.SERVER_ERROR:
                        Log.e(TAG, "Server Error");
                        break;
// For some reason network is not responding
                    case ErrorKeys.NETWORK_ERROR:
                        Log.e(TAG, "Network Error");
                        break;
                }
            }
        });
    }




}
