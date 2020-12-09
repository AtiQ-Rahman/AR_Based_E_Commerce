package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.augmented_reality.AR_Based_E_Commerce.CustomAlertDialog;
import com.augmented_reality.AR_Based_E_Commerce.EngToBanConverter;
import com.augmented_reality.AR_Based_E_Commerce.Product;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class AllProducts extends Fragment {

    AlertDialog alertDialog;
    String admin_device_id = "", mail, phone_number = "", admin_id = "";
    ArrayList<Product> products = new ArrayList<Product>();
    ArrayList<Product> products_temp = new ArrayList<Product>();
    ArrayList<String> product_types = new ArrayList<>();
    ArrayList<String> price_range = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();

    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    TextView empty;
    ProgressDialog progressDialog;
    Button add_new_product;
    ArrayList<Product> sold_products = new ArrayList<>();
    RecycleAdapter2 horizontal_recycleAdapter;
    RecyclerView horizontal_recycleview;
    ImageView filter_panel_btn;
    DrawerLayout drawer;
    Spinner product_type_sp, product_price_sp, age_range_sp, color_sp, weight_range_sp, born_sp;
    String product_type = "", color = "", born = "";
    int price_start = 0, price_end = 0, age_start = 0, age_end = 0, weight_start = 0, weight_end = 0;
    int minimum_price;
    RecycleAdapter recycleAdapter;
    Button btn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_products, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycle);
        recycleAdapter = new RecycleAdapter(products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recycleAdapter);
        empty = view.findViewById(R.id.empty);
        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        filter_panel_btn = view.findViewById(R.id.filter_panel_btn);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait");
        horizontal_recycleview = view.findViewById(R.id.horizontal_recycle);
        horizontal_recycleAdapter = new RecycleAdapter2(sold_products);
        horizontal_recycleview.setAdapter(horizontal_recycleAdapter);


        CustomerDashboard.search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_string = s.toString();
                search(search_string);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filter_panel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }

            }
        });

        product_types.add("All");
        product_types.add("Gents");
        product_types.add("Ladies");
        product_types.add("Children");
        product_type_sp = view.findViewById(R.id.product_type);
        product_type_sp.setAdapter(new CustomAdapter(getContext(), 0, product_types));
        product_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    product_type = product_types.get(position);
                } else {
                    product_type = "";
                }
                filter();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        price_range.add("All");
        price_range.add("0-500");
        price_range.add("500-1000");
        price_range.add("1,000-2,000");
        price_range.add("2,000-5,000");
        price_range.add("5,000-10,000");

        product_price_sp = view.findViewById(R.id.price);
        product_price_sp.setAdapter(new CustomAdapter(getContext(), 0, price_range));
        product_price_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {

                    if (position == 1) {
                        price_start = 0;
                        price_end = 500;
                    } else if (position == 2) {
                        price_start = 500;
                        price_end = 1000;
                    } else if (position == 3) {
                        price_start = 1000;
                        price_end = 2000;
                    } else if (position == 4) {
                        price_start = 2000;
                        price_end = 5000;
                    } else if (position == 5) {
                        price_start = 50000;
                        price_end = 10000;
                    }

                    filter();
                } else {
                    price_start = 0;
                    price_end = 6000000;
                    filter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        colors.add(getString(R.string.all));
        colors.add(getString(R.string.white));
        colors.add(getString(R.string.black));
        colors.add(getString(R.string.red));
        colors.add(getString(R.string.brown));
        colors.add(getString(R.string.gray));
        colors.add(getString(R.string.mixed));
        color_sp=view.findViewById(R.id.color);
        color_sp.setAdapter(new CustomAdapter(getContext(),0,colors));
        color_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){
                    color=colors.get(position);
                    filter();
                }
                else{
                    color="";
                    filter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public void filter() {
        products.clear();
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
        for (int i = 0; i < products_temp.size(); i++) {
            Product product = products_temp.get(i);

            //type filter
            if (product_type.length() == 0 || product_type.equalsIgnoreCase(product.product_type)) {
                products.add(product);
            } else if (!product_type.equalsIgnoreCase(product.product_type)) {
                products.remove(product);
            }
            //price filter
            if (((product.price >= price_start && product.price <= price_end)) && !products.contains(product)) {
                // products.add(product);

            } else if ((product.price < price_start || product.price > price_end)) {
                products.remove(product);
            }

            //color filter
            if(color.equalsIgnoreCase(product.color)&&!products.contains(product))
            {
                // products.add(product);
            }
            else if(color.length()>0&&!color.equalsIgnoreCase(product.color)){
                products.remove(product);
            }

        }
        recycleAdapter.notifyDataSetChanged();
    }

    public void search(String search_string){

        products.clear();
        search_string=search_string.toLowerCase().trim();
        for(int i=0;i<products_temp.size();i++){
            Product product=products_temp.get(i);
            String product_id="A-"+product.product_alt_id;
            product_id=product_id.toLowerCase();
            String product_name=product.name.toLowerCase();
            if(product_id.startsWith(search_string)||product_name.startsWith(search_string)){
                products.add(product);
            }
        }
        filter();
        //recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        get_all_products_data();
        get_AppConfigurationData();
    }

    public class RecycleAdapter2 extends RecyclerView.Adapter<RecycleAdapter2.ViewAdapter>{

        ArrayList<Product> products;
        public RecycleAdapter2(ArrayList<Product> products){
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
                product_image=mView.findViewById(R.id.product_image1);
                image_name=mView.findViewById(R.id.image_name);
                item=mView.findViewById(R.id.item_layout);
                option_menu=mView.findViewById(R.id.option_btn);
            }


            @Override
            public void onClick(View v) {

                int position=getLayoutPosition();
                Product product=products.get(position);
                Intent tnt=new Intent(getContext(),Product_Details.class);
                tnt.putExtra("product_id",product.product_id);
                startActivity(tnt);

            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.product_sold,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            Product product=products.get(position);
            if(product.image_path.length()>0){
                holder.item.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.product_image.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                Picasso.get().load(product.image_path).into(holder.product_image);
                //holder.image_name.setVisibility(View.GONE);
                holder.image_name.setText(getString(R.string.sold)+"\n"+ EngToBanConverter.getInstance().convert(product.price+"") +" "+getString(R.string.taka));
            }
            holder.option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });



        }

        @Override
        public int getItemCount() {
            return products.size();
        }



    }

    public static class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> user_types;
        LayoutInflater inflter;
        int flag;

        public CustomAdapter(Context applicationContext, int flag, ArrayList<String> user_types) {
            this.context = applicationContext;
            this.flag = flag;
            this.user_types = user_types;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return user_types.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.filter_item_layout, null);
            final TextView names =view.findViewById(R.id.user_type);
            names.setText(user_types.get(i));



            return view;
        }
    }
    
    public void get_all_products_data(){
        progressDialog.show();
        Query documentReference=db.collection("AllProducts");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                products.clear();
                products_temp.clear();
                sold_products.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            String product_id=map.get("product_id").toString();
                            String user_id=map.get("user_id").toString();
                            String name=map.get("name").toString();
                            String product_type=map.get("type").toString();
                            int price=Integer.parseInt(map.get("price").toString());
                            String color=map.get("color").toString();
                            String compress_image_path="";
                            if(map.containsKey("compress_image_path")){
                                compress_image_path=map.get("compress_image_path").toString();
                            }
                            String[] image_paths=map.get("original_image_path").toString().split(",");
                            String image_path=image_paths[0];
                            String video_path="";
                            if(map.containsKey("video_path")){
                                video_path=map.get("video_path").toString();
                            }

                            String product_alt_id=map.get("alternative_id").toString();
                            String sold_status=map.get("sold_status").toString();
                            Product product=new Product(product_id,product_type,product_alt_id,user_id,name,price,color,image_path,video_path);
                            int sold_price=0;
                            if(!sold_status.equalsIgnoreCase("unsold")&&map.containsKey("sold_price")){
                                sold_price=Integer.parseInt(map.get("sold_price").toString());

                                Product product2=new Product(product_id,product_type,product_alt_id,user_id,sold_status,sold_price,name,price,color,compress_image_path,video_path);
                                sold_products.add(product2);
                            }
                            else{
                                products.add(product);
                                get_user_data(product.user_id,product);
                            }
                        }
                        products_temp.addAll(products);
                        Collections.sort(products);
                        Collections.sort(products_temp);
                        horizontal_recycleAdapter.notifyDataSetChanged();
                        recycleAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.INVISIBLE);
                        //born_sp.setAdapter(new CustomAdapter(getContext(),0,borns));
                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }

                }
                else{

                    recyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);

                }
                progressDialog.dismiss();

            }


        });
    }
//------------Halum
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<Product> products;
        public RecycleAdapter(ArrayList<Product> products){
            this.products=products;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button compare,details,pricing;
            CardView card1;
            ImageView product_image1;
            TextView id_tv,name_tv1,price_tv1,highest_bid_tv1,weight_tv1;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                product_image1=mView.findViewById(R.id.product_image1);
                id_tv=mView.findViewById(R.id.id);
                name_tv1=mView.findViewById(R.id.name1);
                price_tv1=mView.findViewById(R.id.price1);

                card1=mView.findViewById(R.id.card1);
            }


            @Override
            public void onClick(View v) {

                Product product=products.get(getLayoutPosition());
                Intent tnt=new Intent(getContext(),Product_Details.class);
                tnt.putExtra("product_id",product.product_id);
                startActivity(tnt);
            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.product_list_item_for_customer,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Product product=products.get(position);
            holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
            holder.product_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
            holder.name_tv1.setText(getString(R.string.name2) +" : "+product.name);
            holder.price_tv1.setText(getString(R.string.price)+" : "+EngToBanConverter.getInstance().convert(product.price+"") +" "+getString(R.string.taka));
            holder.id_tv.setText("A-"+product.product_alt_id);
            if(product.image_path!=null&&product.image_path.length()>5){
                Picasso.get().load(product.image_path).into(holder.product_image1);
            }
            holder.name_tv1.setSelected(true);
            holder.price_tv1.setSelected(true);
//            holder.compare.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    show_compare_product_list(product.product_id);
//                }
//            });
//            holder.details.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent tnt=new Intent(getContext(),Product_Details.class);
//                    tnt.putExtra("product_id",product.product_id);
//                    startActivity(tnt);
//                }
//            });
//            holder.pricing.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    show_price_writing_dialog(product);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

    }


    public void show_compare_product_list(String product_id){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.compare_dialogbox_layout,null);
        alert.setView(view);
        progressDialog.dismiss();
        alertDialog=alert.show();;
        RecyclerView recyclerView=view.findViewById(R.id.recycle);
        RecycleAdapter_For_Compare recycleAdapter_for_compare=new RecycleAdapter_For_Compare(products,product_id);
        recyclerView.setAdapter(recycleAdapter_for_compare);
        Button cancel=view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });



    }

    public class RecycleAdapter_For_Compare extends RecyclerView.Adapter<RecycleAdapter_For_Compare.ViewAdapter>{

        ArrayList<Product> products;
        String product_id;
        public RecycleAdapter_For_Compare(ArrayList<Product> products,String product_id){
            this.products=products;
            this.product_id=product_id;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder{

            View mView;
            CardView card1,card2;
            ImageView product_image1;
            TextView name_tv1,price_tv1,weight_tv1;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                product_image1=mView.findViewById(R.id.image);
                name_tv1=mView.findViewById(R.id.name);
                price_tv1=mView.findViewById(R.id.price);
                card1=mView.findViewById(R.id.card);
            }


        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.compare_item,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Product product=products.get(position);
            holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
            holder.product_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
            holder.name_tv1.setText(getString(R.string.name2) +" : "+product.name);
            holder.price_tv1.setText(getString(R.string.price)+" : "+product.price+" "+getString(R.string.taka));
            if(product.image_path!=null&&product.image_path.length()>5){
                Picasso.get().load(product.image_path).into(holder.product_image1);
            }
            holder.name_tv1.setSelected(true);
            holder.price_tv1.setSelected(true);
            holder.card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                    Intent tnt=new Intent(getContext(),Compare.class);
                    tnt.putExtra("product_id1",product_id);
                    tnt.putExtra("product_id2",product.product_id);
                    startActivity(tnt);
                }
            });

        }

        @Override
        public int getItemCount() {
            return products.size();
        }

    }
    
    public void show_price_writing_dialog(Product product){

        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.price_writing_layout,null);
        alert.setView(view);
        alertDialog=alert.show();
        Button submit=view.findViewById(R.id.submit);
        Button cancel=view.findViewById(R.id.cancel);
        EditText price_et=view.findViewById(R.id.price);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price_str=price_et.getText().toString();
                if(price_str.length()>0){
                    int price=Integer.parseInt(EngToBanConverter.getInstance().convert_bangla_to_english(price_str));
                    if(price>=minimum_price&&minimum_price<=product.price||minimum_price>product.price){
                        alertDialog.dismiss();
                        upload_new_price(price,product);
                    }
                    else {
                        CustomAlertDialog.getInstance().show_error_dialog(getContext(),getString(R.string.app_name),"পশুটির সর্বনিম্ন দাম "+minimum_price+" "+getString(R.string.taka));
                    }

                }
                else{
                    Toast.makeText(getContext(),"Write Down The Price",Toast.LENGTH_LONG).show();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void upload_new_price(int price,Product product){
        
        /*progressDialog.show();
        if(price>product.price){
            update_product_info(price,product.total_bid+1,product);
        }
        DocumentReference documentReference=db.collection("BidHistory").document();
        Map<String,Object> map=new HashMap<>();
        map.put("seller_id",product.user_id);
        map.put("seller_name",product.seller_name);
        map.put("seller_location",product.seller_location);
        map.put("buyer_id", SharedPrefManager.getInstance(getContext()).getUser().user_id);
        map.put("buyer_name",SharedPrefManager.getInstance(getContext()).getUser().user_name);
        map.put("buyer_location",SharedPrefManager.getInstance(getContext()).getUser().location);
        map.put("product_id",product.product_id);
        map.put("price",price);
        map.put("document_id",documentReference.getId());
        map.put("time", FieldValue.serverTimestamp());
        String document_id=documentReference.getId();
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                NotificationSender.getInstance().createNotification(getString(R.string.new_price_request),SharedPrefManager.getInstance(getContext()).getUser().user_name+" আপনার পশুটি "+ EngToBanConverter.getInstance().convert(price+"")+" টাকায় কিনতে চায়।",user_id,SharedPrefManager.getInstance(getContext()).getUser().user_name,SharedPrefManager.getInstance(getContext()).getUser().image_path,"buyer",product.user_id,document_id,SharedPrefManager.getInstance(getContext()).getUser().device_id,product.seller_device_id,"new price");
                NotificationSender.getInstance().createNotification(getString(R.string.new_price_request),SharedPrefManager.getInstance(getContext()).getUser().user_name+" পশুটি "+ EngToBanConverter.getInstance().convert(price+"")+" টাকায় কিনতে চায়।",user_id,SharedPrefManager.getInstance(getContext()).getUser().user_name,SharedPrefManager.getInstance(getContext()).getUser().image_path,"buyer",admin_id,document_id,SharedPrefManager.getInstance(getContext()).getUser().device_id,admin_device_id,"new price");
                progressDialog.dismiss();
            }
        });

*/
    }


    public void update_product_info(int price,int total_bid,Product product){
        /*
        DocumentReference documentReference=db.collection("AllProducts").document(product.product_id);
        Map<String,Object> map=new HashMap<>();
        map.put("highest_bid",price);
        map.put("total_bid",total_bid);
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                get_all_products_data();
            }
        });*/
    }

    public void get_AppConfigurationData(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AppConfiguration").document("AppConfiguration");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    progressDialog.dismiss();
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists())
                    {
                        Map<String,Object> data=documentSnapshot.getData();
                        String minimum_payment=data.get("minimum_payment").toString();
                        minimum_price =Integer.parseInt(data.get("minimum_price").toString());
                        String expire_time=data.get("confirmation_expire_time").toString();
                        String str=data.get("bkash_account_number").toString()+"("+data.get("bkash_account_number_status")+")";
                        admin_id=data.get("admin_id").toString();
                        get_device_id(admin_id);
                    }

                }
            }
        });
    }
    public void get_device_id(String user_id){
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        if (map.containsKey("device_id")) {

                            admin_device_id=map.get("device_id").toString();
                        }
                    }
                }
            }
        });
    }
    public void get_user_data(String user_id,Product product){
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        String seller_name=map.get("user_name").toString();
                        String address="";
                        if(map.containsKey("address")){
                            address=map.get("address").toString();
                        }
                        String seller_device_id=map.get("device_id").toString();
                        product.setAdmin_device_id(seller_device_id);
                        product.setAdmin_location(address);
                        product.setAdmin_name(seller_name);
                    }
                }
            }
        });
    }

}    


       