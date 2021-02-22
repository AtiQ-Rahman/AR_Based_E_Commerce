package com.augmented_reality.AR_Based_E_Commerce.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.augmented_reality.AR_Based_E_Commerce.Order;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class AllOrdersForAdmin extends Fragment {

    RecyclerView recyclerView;
    TextView orders_tv;
    ArrayList<Order> orders=new ArrayList<>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    public RecycleAdapter recycleAdapter;
    ProgressDialog progressDialog;
    String customer_id="", time="";
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_orders_for_admin, container, false);
        recyclerView= view.findViewById(R.id.recycleView);
        recycleAdapter=new RecycleAdapter(orders,getContext());
        recyclerView.setAdapter(recycleAdapter);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Loading..");
        get_all_data();
        return view;
    }
    public void get_all_data()
    {
        progressDialog.show();
        Query query=db.collection("Orders");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isComplete()){

                    QuerySnapshot queryDocumentSnapshots=task.getResult();

                    for( DocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                        Map<String,Object> data=documentSnapshot.getData();

                        time= toDateStr(((Timestamp)data.get("order_time")).getSeconds()*1000);
                        //data.get("order_time").toString()
                        Order order=new Order(data.get("order_id").toString(),data.get("product_name").toString(),data.get("product_price").toString(),time);

                        orders.add(order);
                    }
                    recycleAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }
        });

    }
    public static String toDateStr(long milliseconds)
    {
        String format="dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder>{
        ArrayList<Order> orders;
        Context context;
        public RecycleAdapter(ArrayList<Order> orders, Context context) {
            this.orders=orders;
            this.context=context;
        }
        public class RecycleViewHolder extends RecyclerView.ViewHolder{

            TextView order_id_tv,product_name_tv,product_price_tv,order_date_tv;

            ImageButton dlt_btn,edit_btn;
            public RecycleViewHolder(@NonNull View itemView) {
                super(itemView);
                order_id_tv=itemView.findViewById(R.id.order_id);
                product_name_tv=itemView.findViewById(R.id.product_name_odr);
                product_price_tv=itemView.findViewById(R.id.product_price);
                order_date_tv=itemView.findViewById(R.id.order_date);
                dlt_btn= itemView.findViewById(R.id.dlt_btn);
                edit_btn = itemView.findViewById(R.id.edit_btn);
            }
        }

        @NonNull
        @Override
        public RecycleAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_admin,parent,false);
            return new RecycleAdapter.RecycleViewHolder(view) ;
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleAdapter.RecycleViewHolder holder, int position) {

            Order order = orders.get(position);
            holder.order_id_tv.setText("Order Id: " + order.order_id);
            holder.product_name_tv.setText("Product Name: " + order.product_name);
            holder.product_price_tv.setText("Product Price: " + order.product_price);
            holder.order_date_tv.setText("Order Date: " + order.order_date);
            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent tnt=new Intent(getContext(),EditOrder.class);
                    tnt.putExtra("order_id", order.order_id);
                    startActivity(tnt);
                    Toast.makeText(context,"Edited!!",Toast.LENGTH_LONG).show();
                }
            });
            holder.dlt_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    delete_order(order.order_id);
                    orders.remove(position);
                    Toast.makeText(context,"Deleted!!",Toast.LENGTH_LONG).show();


                }
            });
        }



        public void delete_order(String order_id)
        {

            FirebaseFirestore db=FirebaseFirestore.getInstance();
            db.collection("Orders").document(order_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context,"Deleted!!",Toast.LENGTH_LONG).show();
                    recycleAdapter.notifyDataSetChanged();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,"Erorr",Toast.LENGTH_LONG).show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return orders.size();
        }


    }

}