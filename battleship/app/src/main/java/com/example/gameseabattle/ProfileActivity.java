package com.example.gameseabattle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gameseabattle.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 1;
    CircleImageView profilePicture;
    Button choose, btnUploadGravatar;
    Button save;
    EditText username;
    TextView wins, losses;

    private Uri mImageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicture = findViewById(R.id.profilePicture);
        choose = findViewById(R.id.btnChooseFromFile);
        btnUploadGravatar = findViewById(R.id.btnUploadGravatar);
        save = findViewById(R.id.btnSave);
        username = findViewById(R.id.username);
        losses = findViewById(R.id.losses);
        wins = findViewById(R.id.wins);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");
        firebaseAuth = FirebaseAuth.getInstance();
        loadProfileInfo();
        choose.setOnClickListener(view -> openFileChooser());

        btnUploadGravatar.setOnClickListener(view -> {
            String hash = makeHash(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            String tempURL = "https://s.gravatar.com/avatar/" + hash + "?s=96";
            Glide.with(getApplicationContext()).load(tempURL).into(profilePicture);
        });

        save.setOnClickListener(view -> {
            String name = username.getText().toString().trim();
            if (name.equals("")) {
                return;
            } else {
                databaseReference.child(firebaseAuth.getUid()).child("username").setValue(name);
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child(firebaseAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals("username")) {
                    username.setText(snapshot.getValue().toString());
                }

                if (snapshot.getKey().equals("imagePath")) {
                    Glide.with(getApplicationContext()).load(snapshot.getValue().toString()).into(profilePicture);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadProfileInfo(){
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                losses.setText(String.valueOf(user.getGamesAmount() - user.getWins()));
                wins.setText(String.valueOf(user.getWins()));
                if (!user.getImagePath().equals("default"))
                    Glide.with(getApplicationContext()).load(user.getImagePath()).into(profilePicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String makeHash(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(email.getBytes("CP1252")));
        } catch (Exception ignored) {
        }
        return null;
    }

    private String hex(byte[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : array) {
            stringBuilder.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return stringBuilder.toString();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (mImageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            storageTask = fileReference.putFile(mImageUri);
            storageTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    databaseReference = FirebaseDatabase.getInstance().getReference("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imagePath", mUri);
                    databaseReference.updateChildren(map);

                } else {
                    Toast.makeText(ProfileActivity.this, "Failed uploading", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(ProfileActivity.this, "Upload is in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        }
    }
}