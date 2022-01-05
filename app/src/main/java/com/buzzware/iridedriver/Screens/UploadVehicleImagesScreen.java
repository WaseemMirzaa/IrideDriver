package com.buzzware.iridedriver.Screens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.buzzware.iridedriver.Adapters.VehicleImagesAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.UploadImageModel;
import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityVehicleImagesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadVehicleImagesScreen extends BaseActivity implements VehicleImagesAdapter.OnImageClickListener {

    ActivityVehicleImagesBinding binding;

    VehicleModel vehicle;

    FirebaseFirestore db;

    ArrayList<UploadImageModel> images;

    UploadImageModel selectedImage = null;

    Uri imageUri = null;

    final int ACCESS_Gallery = 102;

    Boolean isNotFromSignUp = false;

    public static void startVehicleInformation(Context c) {

        c.startActivity(new Intent(c, UploadVehicleImagesScreen.class)
                .putExtra("isNotFromSignUp", true));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityVehicleImagesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        onViewCreated();


    }

    private void onViewCreated() {

        isNotFromSignUp = false;

        getExtrasFromIntent();

//        if (isNotFromSignUp) {

//            binding.btnContinue.setVisibility(View.GONE);

//        }

        setTitle();

        checkVehicleDetails();

        setListeners();
    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() != null) {

            Bundle b = getIntent().getExtras();

            isNotFromSignUp = b.getBoolean("isNotFromSignUp");

        }

    }

    private void setListeners() {

        binding.appBarTitleInclude.backIcon.setOnClickListener(v -> finish());

        binding.btnContinue.setOnClickListener(v -> validateAndMoveToHome());

        binding.appBarTitleInclude.backIcon.setOnClickListener(v -> finish());

    }

    private void validateAndMoveToHome() {

        if (validate()) {

            String isVerified = "notApproved";

            if(validateVerification()) {

                isVerified = "Approved";

            }

            Map<String, Object> map = new HashMap<>();

            map.put("isVerified",isVerified);

            FirebaseInstances.usersCollection
                    .document(getUserId())
                    .update(map);

            finish();

        }

    }

    private boolean validate() {

        if (vehicle == null) {

            showErrorAlert("Please Add Vehicle Data First");

            return false;

        }

        if (images != null && images.size() > 0) {

            for (UploadImageModel image : images) {

                if (image.url == null) {

                    showErrorAlert("Please add all images first");

                    return false;
                }

            }

        }

        return true;
    }

    private void checkVehicleDetails() {

        showLoader();

        db = FirebaseFirestore.getInstance();

        db.collection("Vehicle")
                .whereEqualTo(
                        "userId",
                        getUserId()
                )
                .get()
                .addOnCompleteListener(
                        (OnCompleteListener<QuerySnapshot>)
                                this::parseSnapshot
                );


    }

    private void parseSnapshot(Task<QuerySnapshot> task) {

        hideLoader();

        if (task.getResult() != null || task.getResult().size() > 0) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                vehicle = document.toObject(VehicleModel.class);

                vehicle.id = document.getId();

            }

        }

        initImagesList();
    }

    private void initImagesList() {

        images = new ArrayList<>();

        if (vehicle == null)

            return;

        addInCarUrls();

        addCarUrls();

        addSideUrls();

        addDocumentUrls();

        setAdapter();

    }

    private void setAdapter() {

        binding.picsRV.setLayoutManager(new GridLayoutManager(this, 2));

        binding.picsRV.setAdapter(new VehicleImagesAdapter(this, images, this));

    }

    private void addInCarUrls() {

        if (urlExists(vehicle.backInCarUrl)) {

            images.add(new UploadImageModel("Back In", vehicle.backInCarUrl, "backInCarUrl"));

        } else {

            images.add(new UploadImageModel("Back In", null, "backInCarUrl"));

        }

        if (urlExists(vehicle.frontInCarUrl)) {

            images.add(new UploadImageModel("Front In", vehicle.frontInCarUrl, "frontInCarUrl"));

        } else {

            images.add(new UploadImageModel("Front In", null, "frontInCarUrl"));

        }

    }

    private void addDocumentUrls() {

        if (urlExists(vehicle.insuranceUrl)) {

            images.add(new UploadImageModel("Insurance", vehicle.insuranceUrl, "insuranceUrl"));

        } else {

            images.add(new UploadImageModel("Insurance", null, "insuranceUrl"));

        }

        if (urlExists(vehicle.licenseUrl)) {

            images.add(new UploadImageModel("License", vehicle.licenseUrl, "licenseUrl"));

        } else {

            images.add(new UploadImageModel("License", null, "licenseUrl"));

        }

        if (urlExists(vehicle.registrationUrl)) {

            images.add(new UploadImageModel("Registration", vehicle.registrationUrl, "registrationUrl"));

        } else {

            images.add(new UploadImageModel("Registration", null, "registrationUrl"));

        }

    }


    private void addCarUrls() {

        if (urlExists(vehicle.frontCarUrl)) {

            images.add(new UploadImageModel("Front", vehicle.frontCarUrl, "frontCarUrl"));

        } else {

            images.add(new UploadImageModel("Front", null, "frontCarUrl"));

        }

        if (urlExists(vehicle.rearCarUrl)) {

            images.add(new UploadImageModel("Rear", vehicle.rearCarUrl, "rearCarUrl"));

        } else {

            images.add(new UploadImageModel("Rear", null, "rearCarUrl"));

        }

    }

    private void addSideUrls() {

        if (urlExists(vehicle.leftCarUrl)) {

            images.add(new UploadImageModel("Left", vehicle.leftCarUrl, "leftCarUrl"));

        } else {

            images.add(new UploadImageModel("Left", null, "leftCarUrl"));

        }

        if (urlExists(vehicle.rightCarUrl)) {

            images.add(new UploadImageModel("Right", vehicle.rightCarUrl, "rightCarUrl"));

        } else {

            images.add(new UploadImageModel("Right", null, "rightCarUrl"));

        }

    }

    private boolean urlExists(String backInCarUrl) {

        return backInCarUrl != null && !backInCarUrl.isEmpty() && !backInCarUrl.equalsIgnoreCase("null");

    }

    private void setTitle() {

        binding.appBarTitleInclude.backAppBarTitle.setText("Documents");

    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dispatchTakePictureLauncher.launch(takePictureIntent);

    }

    public void showImagePickerDialog() {

        // setup the alert builder

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose");

        // add a list

        String[] animals = {"Camera", "Gallery"};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    dispatchTakePictureIntent();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });

        // create and show the alert dialog

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), ACCESS_Gallery);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();

            UploadImage();
        }
    }

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Bitmap photo = null;

                    if (result.getData() != null) {

                        photo = (Bitmap) result.getData().getExtras().get("data");

                        imageUri = getImageUri(UploadVehicleImagesScreen.this, photo);

                        UploadImage();

                    }

                }
            });

    private void UploadImage() {

        if (selectedImage == null) {

            hideLoader();

            return;
        }

        showLoader();

        String randomKey = UUID.randomUUID().toString();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomKey);

        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            //Toast.makeText(this, "Save Successfully!", Toast.LENGTH_SHORT).show();
            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                hideLoader();

                db.collection("Vehicle").document(vehicle.id).update(selectedImage.key, uri1.toString());

                imageUri = null;

                selectedImage = null;

                checkVehicleDetails();
            });
        }).addOnFailureListener(e -> {

            hideLoader();

            showErrorAlert(e.getLocalizedMessage());

        });
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }

    @Override
    public void onImageTapped(UploadImageModel imageModel) {

        selectedImage = imageModel;

        checkPermissions();

    }

    private void checkPermissions() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                showImagePickerDialog();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                showPermissionsDeniedError(getString(R.string.camera_permissions_denied_string));

            }
        });
    }

    Boolean validateVerification() {

        if (!hasImage(vehicle.backInCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.frontCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.rearCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.frontInCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.leftCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.rightCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.registrationUrl)) {

            return false;

        }

        if (!hasImage(vehicle.insuranceUrl)) {

            return false;

        }

        if (!hasImage(vehicle.backInCarUrl)) {

            return false;

        }

        if (vehicle.getName().isEmpty()) {

            return false;

        }

        if (vehicle.getYear().isEmpty()) {

            return false;

        }

        if (vehicle.getNoOfDoors().isEmpty()) {

            return false;

        }

        if (vehicle.getMake().isEmpty()) {

            return false;

        }

        if (vehicle.getNoOfSeatBelts().isEmpty()) {

            return false;

        }

        if (vehicle.getTagNumber().isEmpty()) {

            return false;

        }

        if (vehicle.getModel().isEmpty()) {

            return false;

        }

        return true;
    }

    Boolean hasImage(String image) {

        return !(image == null || image.isEmpty() || image.equalsIgnoreCase("null"));
    }
}
