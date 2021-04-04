package com.augmented_reality.AR_Based_E_Commerce.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.augmented_reality.AR_Based_E_Commerce.Message;
import com.augmented_reality.AR_Based_E_Commerce.Messenger;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.augmented_reality.AR_Based_E_Commerce.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MessageList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Message> messages = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public RecycleAdapter recycleAdapter;
    ProgressDialog progressDialog;
    String  time = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        //customer_id = SharedPrefManager.getInstance(getContext()).getUser().user_id;

        recyclerView = view.findViewById(R.id.recycleView);
        recycleAdapter=new RecycleAdapter(messages,getContext());
        recyclerView.setAdapter(recycleAdapter);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        get_all_messages();
        System.out.println("halum ALL----------->"+messages);
        return view;
    }

    public void get_all_messages() {
        progressDialog.show();
        Query query = db.collection("AllMessages");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isComplete()) {
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        Map<String, Object> data = documentSnapshot.getData();


                            //data.get("order_time").toString()
                            if(data.get("receiver_id").toString().equalsIgnoreCase("3mRRdRp6ZHOGg70Wfw5KXohMyHw1")) {
                                time = toDateStr(((Timestamp) data.get("time")).getSeconds() * 1000);
                                Message message = new Message(data.get("sender_id").toString(), data.get("receiver_id").toString(), data.get("receiver_device_id").toString(), data.get("message").toString(), time);
                                messages.add(message);
                            }
                    }
                    recycleAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }
        });


       //Toast.makeText(getApplicationContext(), (CharSequence) Messages, Toast.LENGTH_SHORT).show();
    }

    public static String toDateStr(long milliseconds) {
        String format = "dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {
        ArrayList<Message> messages;
        Context context;

        public RecycleAdapter(ArrayList<Message> messages, Context context) {
            this.messages = messages;
            this.context = context;
        }

        public class RecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView customer_id_tv,msg_tv,msg_date_tv;
            LinearLayout frame;

           // ImageButton dlt_btn, edit_btn;

            public RecycleViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                customer_id_tv = itemView.findViewById(R.id.customer_id);
                msg_tv = itemView.findViewById(R.id.msg);
                msg_date_tv = itemView.findViewById(R.id.msg_date);
                frame = itemView.findViewById(R.id.Message_frame);
//                dlt_btn= itemView.findViewById(R.id.dlt_btn);
//                edit_btn = itemView.findViewById(R.id.edit_btn);
            }
            @Override
            public void onClick(View v) {

               int position=getAdapterPosition();
               Message message=messages.get(position);
                Intent tnt=new Intent(getContext(), Messenger.class);
                tnt.putExtra("sender_id",message.receiver_id);
                tnt.putExtra("receiver_id",message.sender_id);
                tnt.putExtra("sender_type", SharedPrefManager.getInstance(getContext()).getUser().getUser_type());
                tnt.putExtra("receiver_type","Customer");
                tnt.putExtra("sender_device_id",SharedPrefManager.getInstance(getContext()).getUser().getDevice_id());
                tnt.putExtra("receiver_device_id",message.receiver_device_id);
                startActivity(tnt);

            }
        }

        @NonNull
        @Override
        public RecycleAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages, parent, false);
            return new RecycleAdapter.RecycleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.customer_id_tv.setText("Customer ID: " + message.sender_id);
            holder.msg_tv.setText("Message: " + message.message);
            holder.msg_date_tv.setText("Date: " + message.time);



        }


        @Override
        public int getItemCount() {
            return messages.size();
        }


    }
}