package com.augmented_reality.AR_Based_E_Commerce.Admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Product_Details_Admin extends AppCompatActivity {

    ArrayList<String[]> imagesPathList=new ArrayList<>();
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    public String product_id="",user_id="";
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Product product;
    ImageView imageView;
    TextView tv_price,tv_name,tv_color,id_tv,type_tv;
    RecyclerView price_history_recycle;
    LinearLayout price_history_layout;
    EditText price_et;
    User user;
    AlertDialog alertDialog;
    int price=0,previous_price=0;
    VideoView videoView;
    MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details__admin);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        product_id=getIntent().getStringExtra("product_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        imageView=findViewById(R.id.image_view);
        tv_name=findViewById(R.id.name);
        tv_price=findViewById(R.id.price);
        tv_color=findViewById(R.id.color);
        videoView=findViewById(R.id.video_view);
        id_tv=findViewById(R.id.id);

        type_tv=findViewById(R.id.type_admin);

        recycleAdapter=new RecycleAdapter(imagesPathList);
        mediaController=new MediaController(this);
        recyclerView.setAdapter(recycleAdapter);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(videoView.isPlaying()){
                    videoView.pause();
                }
                else{
                    videoView.resume();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_product_data();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void read_details(View view){

        Intent tnt=new Intent(getApplicationContext(), Product_Details_Admin.class);
        tnt.putExtra("product_id",product_id);
        startActivity(tnt);
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
                    String name=map.get("name").toString();
                    int price=Integer.parseInt(map.get("price").toString());

                    String color=map.get("color").toString();


                    String compress_image_path=map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                   // System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();

                    String product_type=product_type=map.get("type").toString();
                    String product_alt_id=map.get("alternative_id").toString();
                    product=new Product(product_id,product_alt_id,user_id,"",name,price,color,image_path,compress_image_path,video_path);
                    for(int i=0;i<image_paths.length;i++){
                        String[] str={image_paths[i],"image"};
                        imagesPathList.add(str);
                    }
                    if(video_path.length()>5){
                        String[] str={video_path,"video"};
                        imagesPathList.add(str);
                    }

                    recycleAdapter.notifyDataSetChanged();
                    id_tv.setText("A-"+product_alt_id);
                    tv_name.setText(name);

                    tv_price.setText(price+" "+getString(R.string.taka));


                    tv_color.setText(color);

                    type_tv.setText(product_type);

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

                        user=new User(map.get("user_id")+"",map.get("user_name")+"",map.get("user_type")+"",map.get("phone_number")+"",map.get("image_path")+"",map.get("device_id")+"" );

                    }
                }
            }
        });

    }


    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<String[]> products;
        public RecycleAdapter(ArrayList<String[]> products){
            this.products=products;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button option_menu;
            TextView image_name;
            RelativeLayout item;
            ImageView product_image;
            LinearLayout play_btn;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                product_image=mView.findViewById(R.id.product_image);
                image_name=mView.findViewById(R.id.image_name);
                item=mView.findViewById(R.id.item_layout);
                option_menu=mView.findViewById(R.id.option_btn);
                play_btn=mView.findViewById(R.id.play_btn);
            }

            @Override
            public void onClick(View v) {
                int position =getLayoutPosition();
                String[] image_path=products.get(position);
                if(image_path[0].length()>0){
                    videoView.setVisibility(View.GONE);
                    Picasso.get().load(image_path[0]).into( imageView);
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


            String[] image_path=products.get(position);
            holder.image_name.setVisibility(View.GONE);
            holder.option_menu.setVisibility(View.GONE);
            if(image_path[0].length()>0&&image_path[1].equalsIgnoreCase("image")){
                holder.play_btn.setVisibility(View.GONE);
                holder.item.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
                holder.product_image.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));
                Picasso.get().load(image_path[0]).into( holder.product_image);
            }
            else if(image_path[0].length()>0&&image_path[1].equalsIgnoreCase("video")){
                holder.play_btn.setVisibility(View.VISIBLE);

            }
            if(position==0&&image_path[0].length()>0){
                Picasso.get().load(image_path[0]).into(imageView);
            }
            holder.play_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.setVideoPath(image_path[0]);
                    videoView.setVisibility(View.VISIBLE);
                    progressDialog.show();
                    videoView.start();
                    videoView.requestFocus();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            progressDialog.dismiss();
                        }
                    });
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            progressDialog.dismiss();
                            return false;
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }

    public void load_image(CircleImageView circleImageView,String user_id){
        DocumentReference  documentReference= db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    Map<String,Object> map=documentSnapshot.getData();
                    if(map.containsKey("image_path")){
                        String image_path=map.get("image_path").toString();
                        if(image_path.length()>5){
                            Picasso.get().load(image_path).into(circleImageView);
                        }
                    }
                }
            }
        });
    }

    public void edit(View view){

        Intent intent=new Intent(getApplicationContext(),EditProductInfo.class);
        intent.putExtra("product_id",product_id);
        startActivity(intent);

    }
    public void delete(View view){

        show_exit_dialog(Product_Details_Admin.this);

    }

    public void yes_delete(){

        progressDialog.show();
        DocumentReference  documentReference= db.collection("AllProducts").document(product_id);
        documentReference.delete();
        StorageReference storageReference=null;
        imagesPathList.remove(imagesPathList.size()-1);
        for(int i=0;i<imagesPathList.size();i++){
            if(imagesPathList.get(i)[0].length()>5){
                storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(imagesPathList.get(i)[0]);
                storageReference.delete();
            }

        }
        if(product.compress_image_path.length()>5){
            storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(product.compress_image_path);
            storageReference.delete();
        }
        if(product.video_path.length()>0){
            storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(product.video_path);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
        progressDialog.dismiss();
        finish();
    }

    public void show_exit_dialog(Context context){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.exit_panel,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button yes=view.findViewById(R.id.yes);
        Button no=view.findViewById(R.id.no);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.delete_permission);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                yes_delete();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
