package petsnetwork.juanka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import petsnetwork.juanka.android.AdoptionActivity;
import petsnetwork.juanka.android.R;

// Clase para enviar y añadir un perro en adopcion
public class AddDogAdopcion extends AppCompatActivity {
    private EditText EdtxAge, EdtxDogName, EdtxDogRace, EdtxContact, EdtxDescription, EdtxDogLocation;
    private ImageView DogImageAdoption;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference AdoptionRef, UsersRef;
    private StorageReference ImageRef;
    String currentUserID;
    final static int Gallery_Pick = 1;
    private ProgressDialog loadingBar;
    private long contador_posts = 0;
    private String downloadUrl;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_perro_adopcion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        AdoptionRef = FirebaseDatabase.getInstance().getReference().child("Adopciones");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ImageRef = FirebaseStorage.getInstance().getReference().child("adoption_images");

        //Edittexts
        EdtxAge = (EditText) findViewById(R.id.dog_edad);
        EdtxDogName = (EditText) findViewById(R.id.dog_name);
        EdtxDogRace = (EditText) findViewById(R.id.dog_race);
        EdtxContact = (EditText) findViewById(R.id.dog_contact);
        EdtxDescription = (EditText) findViewById(R.id.dog_description);
        EdtxDogLocation = (EditText) findViewById(R.id.dog_location);
        DogImageAdoption = (ImageView) findViewById(R.id.dog_imageview);

        DogImageAdoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Gallery_Pick);
            }
        });

        FloatingActionButton fab = findViewById(R.id.add_dog_adoption);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDogAdoption();
                Snackbar.make(view, "Añadido correctamente", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void AddDogAdoption() {
        final String dog_name = EdtxDogName.getText().toString();
        final String dog_age = EdtxAge.getText().toString();
        final String dog_race = EdtxDogRace.getText().toString();
        final String dog_contact = EdtxContact.getText().toString();
        final String dog_description = EdtxDescription.getText().toString();
        final String dog_location = EdtxDogLocation.getText().toString();

        // Hora y dia que se hace la publicacion y enviarla a Firebase
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());


        postRandomName = saveCurrentDate + saveCurrentTime;

        AdoptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    contador_posts = dataSnapshot.getChildrenCount();   // Numero de posts


                } else {
                    contador_posts = 0;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();

            }
        });

        if (TextUtils.isEmpty(dog_name)) {
            Toast.makeText(this, "Por favor escribe el nombre del perro en adopcion", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(dog_age)) {
            Toast.makeText(this, "Por favor, introduce la edad del perro", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(dog_race)) {
            Toast.makeText(this, "Por favor, introduce la raza del perro", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(dog_contact)) {
            Toast.makeText(this, "Por favor, introduce el telefono de contacto", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(dog_location)) {
            Toast.makeText(this, "Por favor, introduce la ubicación donde se encuentra el perro", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(dog_description)) {
            Toast.makeText(this, "Por favor, introduce una breve descripcion del caracter del perro y su comportamiento", Toast.LENGTH_SHORT).show();
        } else {
            UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("usuario").getValue().toString();
                        String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();


                        HashMap dogMap = new HashMap();
                        dogMap.put("date", saveCurrentDate);
                        dogMap.put("time", saveCurrentTime);
                        dogMap.put("name", dog_name);
                        dogMap.put("user", userName);
                        dogMap.put("profileimage", userProfileImage);
                        dogMap.put("age", dog_age);
                        dogMap.put("race", dog_race);
                        dogMap.put("contact", dog_contact);
                        dogMap.put("dogimage", downloadUrl);
                        dogMap.put("description", dog_description);
                        dogMap.put("dog_location", dog_location);
                        dogMap.put("contador", contador_posts++);
                        AdoptionRef.child(currentUserID + postRandomName).updateChildren(dogMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    uploadImage();
                                    SendUserToMainActivity();
                                    Toast.makeText(AddDogAdopcion.this, "Se ha añadido correctamente..", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(AddDogAdopcion.this, "Ha ocurrido un error : " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AddDogAdopcion.this, "Ha ocurrido un error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                DogImageAdoption.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo...");
            progressDialog.show();

            final StorageReference filePath = ImageRef.child(currentUserID + ".jpg");
            filePath.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        Toast.makeText(AddDogAdopcion.this, "Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                        downloadUrl = downUri.toString();
                    } else {
                        Toast.makeText(AddDogAdopcion.this, "Por favor selecciona una foto...", Toast.LENGTH_SHORT).show();
                        String message = task.getException().getMessage();
                        Toast.makeText(AddDogAdopcion.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddDogAdopcion.this, AdoptionActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
