package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.augmented_reality.AR_Based_E_Commerce.Admin.ContactForAdmin;
import com.augmented_reality.AR_Based_E_Commerce.AllNotifications;
import com.augmented_reality.AR_Based_E_Commerce.AnimationFactory;
import com.augmented_reality.AR_Based_E_Commerce.CustomAlertDialog;
import com.augmented_reality.AR_Based_E_Commerce.DisabledActivity;
import com.augmented_reality.AR_Based_E_Commerce.R;
import com.augmented_reality.AR_Based_E_Commerce.SelectUserType;
import com.augmented_reality.AR_Based_E_Commerce.SharedPrefManager;
import com.augmented_reality.AR_Based_E_Commerce.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "Main";
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    GeoPoint geoPoint;
    DrawerLayout drawer;
    ImageView menu, menu2;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    Button vaccine;
    TextView user_name_tv;
    CircleImageView profile_picture;
    public ActionBar actionBar;
    String image_path = "";
    ImageView[] indicators = new ImageView[7];
    public TextView title_tv;
    public static EditText search_et;
    Button back_btn;
    public String user_id="";
    public static ImageView search_btn,cart_icon;
    public static TextView message_unseen;
    ImageView notification_btn;
    AlertDialog alertDialog;
    public boolean isNotificationOn=true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        checkDisabled();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        progressDialog=new ProgressDialog(CustomerDashboard.this);
        progressDialog.setMessage("Please Wait...");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        user_name_tv = findViewById(R.id.user_name);
        notification_btn = findViewById(R.id.notification_btn);
        message_unseen = findViewById(R.id.message_unseen);
        profile_picture = findViewById(R.id.profile_picture);
        title_tv = findViewById(R.id.title_bar);
        image_path = SharedPrefManager.getInstance(getApplicationContext()).getUser().image_path;
        user_id=SharedPrefManager.getInstance(getApplicationContext()).getUser().user_id;
        if (image_path != null && image_path.length() > 5) {
            Picasso.get().load(image_path).into(profile_picture);
        }
        indicators[0] = findViewById(R.id.dashboard_active);
        indicators[1] = findViewById(R.id.profile_active);
        indicators[2] = findViewById(R.id.total_item_active);
        indicators[3] = findViewById(R.id.transaction_history_active);
        indicators[4] = findViewById(R.id.contact_active);
        indicators[5] = findViewById(R.id.about_active);

        user_name_tv.setText(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        menu = findViewById(R.id.menu);
        menu2 = findViewById(R.id.menu_icon2);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        search_btn = findViewById(R.id.search_btn);
        title_tv=findViewById(R.id.title_bar);
        search_et=findViewById(R.id.search_et);
        cart_icon = findViewById(R.id.cart);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setVisibility(View.VISIBLE);
                title_tv.setVisibility(View.GONE);
                menu2.setVisibility(View.GONE);
                notification_btn.setVisibility(View.GONE);
                notification_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                menu2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                search_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                title_tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                Animation animation= AnimationFactory.getInstance().right_to_left_scale_anim();
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                search_et.startAnimation(animation);
            }
        });

        search_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search_et.getRight() - search_et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        // search_et.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_reverse));
                        Animation animation=AnimationFactory.getInstance().left_to_right_scale_anim();
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                search_et.setText("");
                                search_et.setVisibility(View.GONE);
                                title_tv.setVisibility(View.VISIBLE);
                                menu2.setVisibility(View.VISIBLE);
                                notification_btn.setVisibility(View.VISIBLE);
                                notification_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                menu2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                search_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                title_tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        search_et.startAnimation(animation);
                        return true;
                    }
                }
                return false;
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(menu2);

            }


        });

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getApplicationContext(), AllNotifications.class);
                startActivity(tnt);
            }
        });

        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        frameLayout = findViewById(R.id.frame_layout);


        changeFragmentView(new AllProducts());
        //-----------LogOUT Function-------

        RelativeLayout logOutCD = findViewById(R.id.logOutCD);
        logOutCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                show_exit_dialog();

            }
        });

        // --------- Dashboard-----------
        RelativeLayout dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_btn.setVisibility(View.VISIBLE);
                //search_btn.setVisibility(View.GONE);
                title_tv.setText("Dashboard");
                active_indicator(0);
                changeFragmentView(new Splash_Fragment());

            }
        });

        //----------- Profile -------------

        RelativeLayout profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.GONE);
                title_tv.setText(R.string.profile);
                active_indicator(1);
                changeFragmentView(new CustomerProfile());

            }
        });


        //---------All Products---------------
        RelativeLayout products = findViewById(R.id.all_products);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.VISIBLE);
                //search_btn.setVisibility(View.GONE);
                title_tv.setText("All Products");
                active_indicator(2);
                changeFragmentView(new AllProducts());

            }
        });

        //---------Transaction History-----------//


        RelativeLayout orders = findViewById(R.id.transaction_history);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.VISIBLE);
                //search_btn.setVisibility(View.GONE);
                title_tv.setText("All Orders");
                active_indicator(3);
                changeFragmentView(new AllOrdersForCustomer());

            }
        });

//--------------Contact----------------
        RelativeLayout contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.VISIBLE);
                //search_btn.setVisibility(View.GONE);
                title_tv.setText("Contact Info");
                active_indicator(4);
                changeFragmentView(new ContactForAdmin());

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        message_unseen = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
       get_all_notifications();
    }

    public void active_indicator(int index) {


        for (int i = 0; i < indicators.length; i++) {
            if (i == index&& indicators[i]!=null) {
                indicators[i].setVisibility(View.VISIBLE);
            }
            else if(indicators[i]!=null) {
                indicators[i].setVisibility(View.INVISIBLE);
            }
        }

    }

    public void changeFragmentView(Fragment fragment) {

        fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        int count = fragmentManager.getBackStackEntryCount();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment)
                .addToBackStack(null).commit();
        drawer.closeDrawer(GravityCompat.START);
    }
    public void get_all_notifications(){
        Query documentReference=db.collection("AllNotifications").whereEqualTo("receiver_id",user_id).whereEqualTo("seen_status","unseen");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        message_unseen.setVisibility(View.VISIBLE);
                        if(querySnapshot.size()<100){
                            message_unseen.setText(querySnapshot.size()+"");
                        }
                        else{
                            message_unseen.setText("99+");
                        }
                    }
                    else{
                        message_unseen.setVisibility(View.GONE);

                    }

                }


            }


        });
    }
    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(CustomerDashboard.this, view);
        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.user_dashboard_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.log_out) {
                    show_exit_dialog();
                }
                if (item.getItemId() == R.id.settings) {
                    show_setting_panel();
                }

                return true;
            }
        });
        popup.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {

        int count = fragmentManager.getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (count == 1) {

            CustomAlertDialog.getInstance().show_exit_dialog(CustomerDashboard.this);
        } else {

            super.onBackPressed();
        }
    }
    public void show_setting_panel(){
        AlertDialog.Builder alert=new AlertDialog.Builder(CustomerDashboard.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.setting_layout,null);
        alert.setView(view);
        alertDialog=alert.show();
        Button submit=view.findViewById(R.id.submit);
        Button cancel=view.findViewById(R.id.cancel);
        RadioButton on=view.findViewById(R.id.on);
        RadioButton off=view.findViewById(R.id.off);
        if(SharedPrefManager.getInstance(getApplicationContext()).getUser().isNotificationOn){
            on.setChecked(true);
        }
        else{
            off.setChecked(true);
        }
        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) isNotificationOn =true;
            }
        });
        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) isNotificationOn =false;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                update_login_status( isNotificationOn);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void checkDisabled() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                if (map.containsKey("disabled"))
                    if ((Boolean) map.get("disabled")) {
                        startActivity(new Intent(getApplicationContext(), DisabledActivity.class));
                        finish();
                    }
            }
        });
    }
    public void  update_login_status(boolean isNotificationOn){
        progressDialog.show();
        User user=SharedPrefManager.getInstance(getApplicationContext()).getUser();
        user.setNotificationOn(isNotificationOn);
        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
        DocumentReference documentReference= db.collection("Users").document(user_id);
        Map<String, Object> data = new HashMap<>();
        data.put("notification",isNotificationOn);

        documentReference.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialog.dismiss();
            }
        });
    }
    public void show_exit_dialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(CustomerDashboard.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.exit_panel,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button yes=view.findViewById(R.id.yes);
        Button no=view.findViewById(R.id.no);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.logout);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
               // LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(getApplicationContext(), SelectUserType.class));

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