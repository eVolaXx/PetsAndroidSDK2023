package petsnetwork.juanka.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

//Clase para rellenar mas datos por parte del usuario a la Red Social
public class SetupActivity extends AppCompatActivity {
    private EditText EdtxUsuario, EdtxCiudad, EdtxNombreMascota,EdtxRazaMascota;
    private FloatingActionButton guardaInformacionUsuario;
    private CircleImageView CircleImageViewProfileImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String currentUserID;
    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileimage");


        EdtxUsuario = (EditText) findViewById(R.id.nombre_usuario);
        EdtxCiudad = (EditText) findViewById(R.id.ciudad);
        EdtxNombreMascota = (EditText) findViewById(R.id.nombre_mascota);
        EdtxRazaMascota = (EditText) findViewById(R.id.dog_race);
        guardaInformacionUsuario = (FloatingActionButton) findViewById(R.id.button_guardar);
        CircleImageViewProfileImage = (CircleImageView) findViewById(R.id.imagen_usuario);
        loadingBar = new ProgressDialog(this);


        guardaInformacionUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInformation();
            }
        });


        CircleImageViewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        //Imagen usuario
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")) {
                        String imagen = dataSnapshot.child("profileimage").getValue().toString();
                        //Libreria para manejar imagenes
                        Picasso.get().load(imagen).placeholder(R.drawable.profile).into(CircleImageViewProfileImage);
                    } else {
                        Toast.makeText(SetupActivity.this, "Por favor selecciona primero una imagen de perfil...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    // Metodo donde el usuario si quiere puede añadir su foto de perfil. Guardar imagen de usuario a la base de datos.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null && data.getData() !=null) {

            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Imagen de Perfil");
                loadingBar.setMessage("Por favor, elige la mejor foto de tu mascota...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Imagen de perfil subida al servidor...", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    UsersRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(SetupActivity.this, "Imagen de perfil subida correctamente...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                    else
                                                    {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Ha ocurrido un error : " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });

                                }
                            });




                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Ha ocurrido un error: La imagen no ha podido ser recortada. Prueba de nuevo.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }

    }





//Guardar informacion del usuario a la Base de datos de Firebase
    private void SaveAccountSetupInformation()
    {
        String usuario = EdtxUsuario.getText().toString();
        String ciudad = EdtxCiudad.getText().toString();
        String nombre_mascota = EdtxNombreMascota.getText().toString();
        String raza_mascota = EdtxRazaMascota.getText().toString();


        if(TextUtils.isEmpty(usuario))
        {
            Toast.makeText(this, "Por favor escribe el nombre de usuario...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(ciudad))
        {
            Toast.makeText(this, "Donde vives ? Escriba su ciudad o provincia...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(ciudad))
        {
            Toast.makeText(this, "Como se llama su mascota ?", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Guardando informacion");
            loadingBar.setMessage("Por favor espere, mientras se crea su cuenta...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("usuario", usuario);
            userMap.put("ciudad", ciudad);
            userMap.put("mascota", nombre_mascota);
            userMap.put("raza", raza_mascota);
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Su cuenta ha sido creada correctamente.", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message =  task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Ha ocurrido un error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
