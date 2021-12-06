package com.buzzware.iridedriver.Screens;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    Uri imageUri = null;

    final int ACCESS_Gallery = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getCurrentUserData();

        setListener();

    }

    private void setListener() {

        binding.editImageIV.setOnClickListener(v -> {
            checkPermissions();
        });

        binding.updateBtn.setOnClickListener(v->{
            uploadDataToFirestore();
        });

    }


    private void getCurrentUserData() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getUid() != null) {

            DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection("Users").document(user.getUid());
            documentReferenceBuisnessUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    User user = value.toObject(User.class);

                    setUserData(user);

                }
            });

        }

    }

    private void setUserData(User user) {

        if (user.image != null && !user.image.isEmpty()) {
            Glide.with(EditProfileActivity.this).load(user.image).apply(new RequestOptions().centerCrop()).into(binding.userImageIV);
        }

        binding.fNameET.setText(user.firstName);
        binding.lNameET.setText(user.lastName);
        binding.emailTV.setText(user.email);
        binding.phoneET.setText(user.phoneNumber);
        binding.cityET.setText(user.city);
        binding.stateET.setText(user.state);
        binding.zipET.setText(user.zipcode);
        binding.addressET.setText(user.address);

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

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dispatchTakePictureLauncher.launch(takePictureIntent);

    }

    private void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), ACCESS_Gallery);

    }

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Bitmap photo = null;

                    if (result.getData() != null) {

                        photo = (Bitmap) result.getData().getExtras().get("data");

                        imageUri = getImageUri(EditProfileActivity.this, photo);

                        binding.userImageIV.setImageBitmap(photo);

                        UploadImage();

                    }

                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();

            binding.userImageIV.setImageURI(imageUri);

            UploadImage();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }


    private void UploadImage() {


        showLoader();

        String randomKey = UUID.randomUUID().toString();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomKey);

        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            //Toast.makeText(this, "Save Successfully!", Toast.LENGTH_SHORT).show();
            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                hideLoader();

                firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("image", uri1.toString());

                imageUri = null;


            });
        }).addOnFailureListener(e -> {

            hideLoader();

            showErrorAlert(e.getLocalizedMessage());

        });
    }

    private void uploadDataToFirestore() {

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", binding.fNameET.getText().toString());
        userData.put("lastName", binding.lNameET.getText().toString());
        userData.put("address", binding.addressET.getText().toString());
        userData.put("phoneNumber", binding.phoneET.getText().toString());
        userData.put("state", binding.stateET.getText().toString());
        userData.put("city", binding.cityET.getText().toString());
        userData.put("zipcode", binding.zipET.getText().toString());

        FirebaseFirestore.getInstance().collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .update(userData);

        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }
}