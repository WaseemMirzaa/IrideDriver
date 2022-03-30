package com.buzzware.iridedriver.Screens;

import static com.buzzware.iridedriver.Fragments.WalletFragment.openCloseDrawer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.buzzware.iridedriver.Models.NotificationAdapter;
import com.buzzware.iridedriver.Models.NotificationModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityNotificationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Notifications extends AppCompatActivity {


    ActivityNotificationsBinding binding;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    List<NotificationModel> notificationList=new ArrayList<>();

    String image="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();

        showRequest();

    }

    private void setListeners() {

        binding.drawerIcon.setOnClickListener(v -> finish());

    }

    private void showRequest() {

        firebaseFirestore.collection("Notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    notificationList.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        try {

                            NotificationModel notification = document.toObject(NotificationModel.class);

                            notification.setId(document.getId());

                            if (notification.isRead != null) {

                                if (notification.isRead.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                    notificationList.add(notification);

                                }

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                    setRecycler();
                }
            }
        });

    }

    private final static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");

    private final static SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date getCurrentWeekDayStartTime() {
        Calendar c = Calendar.getInstance();
        try {/*from  w w  w.  ja v a 2s .com*/
            int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
            c.add(Calendar.DATE, -weekday);
            c.setTime(longSdf.parse(shortSdf.format(c.getTime())+ " 23:59:59"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    private void setRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(Notifications.this);

        binding.notificationRV.setLayoutManager(layoutManager);

        NotificationAdapter normalBottleAdapter = new NotificationAdapter(Notifications.this, notificationList);

        binding.notificationRV.setAdapter(normalBottleAdapter);

    }

    public String getImage(String id) {
        image="";
            DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection("Users").document(id);

            documentReferenceBuisnessUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null) {

                        User user = value.toObject(User.class);
                        image=user.image;

                    }


                }
            });

            return image;


    }


}