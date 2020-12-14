package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.augmented_reality.AR_Based_E_Commerce.R;
import com.sslcommerz.library.payment.model.datafield.MandatoryFieldModel;
import com.sslcommerz.library.payment.model.dataset.TransactionInfo;
import com.sslcommerz.library.payment.model.util.CurrencyType;
import com.sslcommerz.library.payment.model.util.ErrorKeys;
import com.sslcommerz.library.payment.model.util.SdkCategory;
import com.sslcommerz.library.payment.model.util.SdkType;
import com.sslcommerz.library.payment.viewmodel.listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.viewmodel.management.PayUsingSSLCommerz;

public class Payment extends AppCompatActivity  {

    public String TAG="Main";
    TextView paymenTAmount;
    String amount="";
    int randomNum = 100000 + (int)(Math.random() * 999999);
    String transactionID;
    int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        paymenTAmount=findViewById(R.id.amount);
        Intent intent = getIntent();
        //intent.getStringExtra("price");
        price=intent.getIntExtra("price",0);
        paymenTAmount.setText(price+"");
      //  Log.d(TAG, intent.getStringExtra("price"));
    }

    public void sendPayment(View view){

        transactionID=String.valueOf(randomNum);
        MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel("htech5fd7c932ecc06", "htech5fd7c932ecc06@ssl", price+"", transactionID, CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_LIST);

        PayUsingSSLCommerz.getInstance().setData(Payment.this, mandatoryFieldModel, new OnPaymentResultListener() {
            @Override
            public void transactionSuccess(TransactionInfo transactionInfo) {

                if (transactionInfo.getRiskLevel().equals("0")) {

                    Toast.makeText(getApplicationContext(),"Transaction succeeded",Toast.LENGTH_LONG).show();
                }

                else {
                    Log.e(TAG, "Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle());
                    Toast.makeText(getApplicationContext(),"Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle(),Toast.LENGTH_LONG).show();
                }
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
