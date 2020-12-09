package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.augmented_reality.AR_Based_E_Commerce.EngToBanConverter;
import com.augmented_reality.AR_Based_E_Commerce.Product;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class Compare extends AppCompatActivity {
    ArrayList<String> imagesPathList=new ArrayList<>();
    RecyclerView recyclerView;
    public String product_id1="",product_id2="",user_id="";
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Product product;
    ImageView imageView1;
    TextView tv_id1,tv_price,tv_name,tv_color,highest_price_tv;
    ImageView imageView2;
    TextView tv_id2,tv_price2,tv_name2,tv_color2,highest_price_tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        product_id1=getIntent().getStringExtra("product_id1");
        product_id2=getIntent().getStringExtra("product_id2");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        imageView1=findViewById(R.id.image1);
        imageView2=findViewById(R.id.image2);
        tv_id1=findViewById(R.id.id1);
        tv_name=findViewById(R.id.name);
        tv_price=findViewById(R.id.price);

        tv_color=findViewById(R.id.color);


        tv_id2=findViewById(R.id.id2);
        tv_name2=findViewById(R.id.name2);
        tv_price2=findViewById(R.id.price2);



        get_product_data1();
        get_product_data2();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void get_product_data1(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllProducts").document(product_id1);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imagesPathList.clear();
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot.exists()){
                    Map<String,Object> map=documentSnapshot.getData();
                    String product_id=map.get("product_id").toString();
                    String user_id=map.get("user_id").toString();
                    String name=map.get("name").toString();
                    int price=Integer.parseInt(map.get("price").toString());
                    float age=Integer.parseInt(map.get("age").toString());
                    String color=map.get("color").toString();

                    String compress_image_path=map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();

                    String product_alt_id=map.get("alternative_id").toString();
                    String product_type=map.get("type").toString();

                    product=new Product(product_id,product_type,product_alt_id,user_id,name,price,color,image_path,video_path);
                    tv_name.setText(name);
                    tv_price.setText(EngToBanConverter.getInstance().convert(price+"")+" "+getString(R.string.taka));
                    String year=(int)(age/12)+"";
                    String month=(int)(age%12)+"";

                    tv_color.setText(color);

                    tv_id1.setText("A-"+product.product_alt_id);

                    if(image_paths[0].length()>0){
                        Picasso.get().load(image_paths[0]).into(imageView1);
                    }
                }
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    public void get_product_data2(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllProducts").document(product_id2);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imagesPathList.clear();
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot.exists()){
                    Map<String,Object> map=documentSnapshot.getData();
                    String product_id=map.get("product_id").toString();
                    String user_id=map.get("user_id").toString();
                    String name=map.get("name").toString();
                    int price=Integer.parseInt(map.get("price").toString());
                    float age=Integer.parseInt(map.get("age").toString());
                    String color=map.get("color").toString();
                    float weight=Float.parseFloat(map.get("weight").toString());
                    float height=Float.parseFloat(map.get("height").toString());
                    int teeth=Integer.parseInt(map.get("teeth").toString());
                    String born=map.get("born").toString();
                    String compress_image_path=map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();
                    int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                    int total_bid=Integer.parseInt(map.get("total_bid").toString());
                    String product_alt_id=map.get("alternative_id").toString();
                    String product_type=map.get("type").toString();
                    product=new Product(product_id,product_type,product_alt_id,user_id,name,price,color,image_path,video_path);
                    tv_name2.setText(name);
                    tv_price2.setText(EngToBanConverter.getInstance().convert(price+"")+" "+getString(R.string.taka));
                    String year=(int)(age/12)+"";
                    String month=(int)(age%12)+"";
                    tv_color2.setText(color);

                    tv_id2.setText("A-"+product.product_alt_id);
                    highest_price_tv2.setText(EngToBanConverter.getInstance().convert(highest_bid+"")+" "+getString(R.string.taka));
                    if(image_paths[0].length()>0){
                        Picasso.get().load(image_paths[0]).into(imageView2);
                    }
                }
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }


}
