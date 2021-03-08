package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.augmented_reality.AR_Based_E_Commerce.Product;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.augmented_reality.AR_Based_E_Commerce.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Product_Details_Customer extends AppCompatActivity {
    ArrayList<String> imagesPathList=new ArrayList<>();
   // ArrayList<PriceHistoryItem> priceHistoryItems=new ArrayList<>();
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    public String product_id="",user_id="",product_name="",compress_image_path="",color="",type="";
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Product product;
    ImageView imageView;
    TextView tv_price,tv_name,tv_color,id_tv,tv_type;
    int PriceForPayment;
    EditText price_et;
    User user;
    int price=0,previous_price=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        product_id=getIntent().getStringExtra("product_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        imagesPathList.add("");
        imagesPathList.add("");
        imagesPathList.add("");
        imagesPathList.add("");
        recyclerView=findViewById(R.id.recycle);
        imageView=findViewById(R.id.image_view);
        tv_name=findViewById(R.id.name);
        tv_price=findViewById(R.id.price);
        tv_color=findViewById(R.id.color);
        tv_type = findViewById(R.id.type);
        id_tv=findViewById(R.id.id);

        recycleAdapter=new RecycleAdapter(imagesPathList);
        recyclerView.setAdapter(recycleAdapter);
        get_product_data();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static String toDateStr(long milliseconds)
    {
        String format="dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
    public void get_product_data(){
        progressDialog.show();
        Toast.makeText(getApplicationContext(),product_id,Toast.LENGTH_LONG).show();
        DocumentReference documentReference=db.collection("AllProducts").document(product_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imagesPathList.clear();
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot.exists()){
                    Map<String,Object> map=documentSnapshot.getData();
                    String product_id=map.get("product_id").toString();
                    String user_id=map.get("user_id").toString();
                    product_name=map.get("name").toString();
                    int price=Integer.parseInt(map.get("price").toString());
                    PriceForPayment=price;
                    color=map.get("color").toString();




                    compress_image_path = map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    //System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();

                    String product_alt_id=map.get("alternative_id").toString();
                    type=map.get("type").toString();
                    product=new Product(product_id,type,product_alt_id,user_id,product_name,price,color,image_path,video_path);
                    imagesPathList.addAll(Arrays.asList(image_paths));
                    recycleAdapter.notifyDataSetChanged();
                    tv_name.setText(product_name);
                    id_tv.setText("A-"+product_alt_id);
                    tv_price.setText(price+" "+getString(R.string.taka));
                    tv_type.setText(type);
                    tv_color.setText(color);
                    get_user_data(user_id);
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
    public void get_user_data(String user_id){

        DocumentReference documentReference= db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String,Object> map=document.getData();
                        String user_type=map.get("user_type").toString();
                        String location="";
                        if(map.containsKey("location")){
                            location=map.get("location").toString();
                        }
                        user=new User(map.get("user_id")+"",map.get("user_name")+"",map.get("user_type")+"",map.get("phone_number")+"",map.get("image_path")+"",map.get("device_id")+"",location);

                    }
                }
            }
        });

    }

    public void makePayment(View view){

        Intent intent=new Intent(getApplicationContext(), Payment.class);
        intent.putExtra("product_id",product_id);
        intent.putExtra("price",PriceForPayment);
        intent.putExtra("compress_image_path",compress_image_path);
        intent.putExtra("product_name",product_name);
        intent.putExtra("product_color",color);
        intent.putExtra("product_type",type);

        startActivity(intent);

    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<String> products;
        public RecycleAdapter(ArrayList<String> products){
            this.products=products;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button option_menu;
            TextView image_name;
            RelativeLayout item;
            ImageView product_image;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                product_image=mView.findViewById(R.id.product_image);
                image_name=mView.findViewById(R.id.image_name);
                item=mView.findViewById(R.id.item_layout);
                option_menu=mView.findViewById(R.id.option_btn);
            }

            @Override
            public void onClick(View v) {
                int position =getLayoutPosition();
                String image_path=products.get(position);
                if(image_path.length()>0){
                    Picasso.get().load(image_path).into( imageView);
                }
            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.product_images,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            String image_path=products.get(position);
            holder.image_name.setVisibility(View.GONE);
            holder.option_menu.setVisibility(View.GONE);
            if(image_path.length()>0){
                holder.item.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
                holder.product_image.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));
                Picasso.get().load(image_path).into( holder.product_image);
            }
            if(position==0&&image_path.length()>0){
                Picasso.get().load(image_path).into(imageView);
            }
        }

        @Override
        public int getItemCount() {
            return products.size();
        }


    }
}
