package com.augmented_reality.AR_Based_E_Commerce.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.augmented_reality.AR_Based_E_Commerce.Product;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


public class AllProductsForAdmin extends Fragment {

    ArrayList<ArrayList<Product>> products=new ArrayList<ArrayList<Product>>();
    ArrayList<Product> products_temp=new ArrayList<Product>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    TextView empty;
    ProgressDialog progressDialog;
    Button add_new_product;
    int product_alt_id=0;
    RecycleAdapter recycleAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all_products_for_admin, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        recyclerView=view.findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recycleAdapter);
        add_new_product=view.findViewById(R.id.add_new_product);
        empty=view.findViewById(R.id.empty);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait");
        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getContext(),Add_New_Product.class);
                tnt.putExtra("product_alt_id",product_alt_id);
                startActivity(tnt);
            }
        });
        AdminDashboard.search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_string=s.toString();
                search(search_string);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }
    public void search(String search_string){

        products.clear();
        search_string=search_string.toLowerCase().trim();

        ArrayList<Product> products2=new ArrayList<>();

        for(int i=0;i<products_temp.size();i++){
            Product product=products_temp.get(i);
            String product_id="A-"+product.product_alt_id;
            product_id=product_id.toLowerCase();
            String product_name=product.name.toLowerCase();
            if(product_id.startsWith(search_string)||product_name.startsWith(search_string)){
                products2.add(product);
                if(products2.size()==2){
                    products.add(products2);
                    products2=new ArrayList<>();
                }
            }
        }
        if(products2.size()>0) products.add(products2);
        recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        get_all_products_data();
        get_product_alt_id();
    }
    public void get_product_alt_id(){
        Query documentReference=db.collection("AllProducts");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isComplete()) {

                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && querySnapshot.size() > 0) {
                        product_alt_id=querySnapshot.size();
                    }
                }
            }
        });

    }

    public void get_all_products_data(){
        progressDialog.show();
        Query documentReference=db.collection("AllProducts");
        documentReference.whereEqualTo("user_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                products.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        ArrayList<Product> products2=new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            String product_id=map.get("product_id").toString();
                            String user_id=map.get("user_id").toString();
                            String name=map.get("name").toString();
                            int price=Integer.parseInt(map.get("price").toString());

                            String color=map.get("color").toString();

                            String image_path="";
                            if(map.containsKey("compress_image_path")){
                                image_path=map.get("compress_image_path").toString();
                            }
                            if(image_path.equalsIgnoreCase("")){
                                String[] image_paths=map.get("original_image_path").toString().split(",");
                               if(image_paths.length>=1) image_path=image_paths[0];
                               else image_path="";
                            }
                            String video_path="";
                            if(map.containsKey("video_path")){
                                video_path=map.get("video_path").toString();
                            }

                            String product_alt_id=map.get("alternative_id").toString();
                            int sold_price=0;
                            String sold_status=map.get("sold_status").toString();
                            if(map.containsKey("sold_price")){
                                sold_price=Integer.parseInt(map.get("sold_price").toString());
                            }
                            String product_type=map.get("type").toString();
                            Product product=new Product(product_id,product_type,product_alt_id,user_id,sold_status,sold_price,name,price,color,image_path,video_path);
                            products_temp.add(product);
                            products2.add(product);
                            if(products2.size()==2){
                                products.add(products2);
                                products2=new ArrayList<>();
                            }


                        }
                        if(products2.size()>0){
                            products.add(products2);
                        }
                        recycleAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.INVISIBLE);

                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        recycleAdapter.notifyDataSetChanged();
                    }

                }
                else{

                    recyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    recycleAdapter.notifyDataSetChanged();

                }
                progressDialog.dismiss();

            }


        });
    }
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<ArrayList<Product>> products;
        public RecycleAdapter(ArrayList<ArrayList<Product>> products){
            this.products=products;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button Bdetails1,Bdetails2;
            CardView card1,card2;
            ImageView product_image1,product_image2;
            TextView id_tv1,id_tv2,name_tv1,name_tv2,price_tv1,price_tv2,color_tv1,color_tv2,type_tv1,type_tv2;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                product_image1=mView.findViewById(R.id.product_image1);
                product_image2=mView.findViewById(R.id.product_image2);
                id_tv1=mView.findViewById(R.id.id);
                id_tv2=mView.findViewById(R.id.id2);
                name_tv1=mView.findViewById(R.id.name1);
                name_tv2=mView.findViewById(R.id.name2);
                price_tv1=mView.findViewById(R.id.price1);
                price_tv2=mView.findViewById(R.id.price2);


                type_tv1=mView.findViewById(R.id.type1_admin);
                type_tv2=mView.findViewById(R.id.type2_admin);

                Bdetails1 = mView.findViewById(R.id.Product_Details1);
                Bdetails2 = mView.findViewById(R.id.Product_Details2);
                card1=mView.findViewById(R.id.card1);
                card2=mView.findViewById(R.id.card2);
            }


            @Override
            public void onClick(View v) {


            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.product_list_item_for_admin,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            ArrayList<Product> products2=products.get(position);
            if(products2.size()==2){

                holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.card2.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.product_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                holder.product_image2.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                Product product=products2.get(0);
                holder.id_tv1.setText("A-"+product.product_alt_id);
                holder.name_tv1.setText(getString(R.string.name2) +" : "+product.name);
                holder.price_tv1.setText(getString(R.string.price)+" : "+product.price+" "+getString(R.string.taka));
                holder.type_tv1.setText("Type"+" : "+product.product_type);

                if(product.image_path!=null&&product.image_path.length()>5){
                    Picasso.get().load(product.image_path).into(holder.product_image1);
                }
                holder.name_tv1.setSelected(true);
                holder.price_tv1.setSelected(true);
                holder.type_tv1.setSelected(true);


                holder.card2.setVisibility(View.VISIBLE);
                product=products2.get(1);
                holder.id_tv2.setText("A-"+product.product_alt_id);
                holder.name_tv2.setText(getString(R.string.name2) +" : "+product.name);
                holder.price_tv2.setText(getString(R.string.price)+" : "+product.price+" "+getString(R.string.taka));
                holder.type_tv2.setText("Type"+" : "+product.product_type);
                if(product.image_path!=null&&product.image_path.length()>5){
                    Picasso.get().load(product.image_path).into(holder.product_image2);
                }
                holder.name_tv2.setSelected(true);
                holder.price_tv2.setSelected(true);
                holder.type_tv2.setSelected(true);

            }
            else if(products2.size()==1){
                holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.product_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                Product product=products2.get(0);
                holder.id_tv1.setText("A-"+product.product_alt_id);
                holder.name_tv1.setText(getString(R.string.name2) +" : "+product.name);
                holder.price_tv1.setText(getString(R.string.price)+" : "+product.price+" "+getString(R.string.taka));
                holder.type_tv1.setText("Type"+" : "+product.product_type);

                if(product.image_path!=null&&product.image_path.length()>5){
                    Picasso.get().load(product.image_path).into(holder.product_image1);
                }

                holder.name_tv1.setSelected(true);
                holder.price_tv1.setSelected(true);
                holder.type_tv1.setSelected(true);

                holder.card2.setVisibility(View.INVISIBLE);
            }

            holder.card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(), Product_Details_Admin.class);
                    tnt.putExtra("product_id",products2.get(0).product_id);
                    startActivity(tnt);
                }
            });
            holder.card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(), Product_Details_Admin.class);
                    tnt.putExtra("product_id",products2.get(1).product_id);
                    startActivity(tnt);
                }
            });

            holder.Bdetails1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(), Product_Details_Admin.class);
                    tnt.putExtra("product_id",products2.get(0).product_id);
                    startActivity(tnt);
                }
            });

            holder.Bdetails2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(), Product_Details_Admin.class);
                    tnt.putExtra("product_id",products2.get(1).product_id);
                    startActivity(tnt);
                }
            });

        }

        @Override
        public int getItemCount() {
            return products.size();
        }



    }
}
