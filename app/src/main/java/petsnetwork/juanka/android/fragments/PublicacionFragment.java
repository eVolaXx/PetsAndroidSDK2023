package petsnetwork.juanka.android.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import petsnetwork.juanka.android.MainActivity;
import petsnetwork.juanka.android.R;
import petsnetwork.juanka.android.core.ImageCompressTask;
import petsnetwork.juanka.android.listeners.IImageCompressTaskListener;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicacionFragment extends Fragment {
    private Toolbar mToolbar;
    private CircleImageView imagenPerfilUsuario;
    private TextView nombre_usuario_new_post;
    private ImageView SelectPostImage;
    private Button btnPublicar;
    private EditText extDescripcion;
    private static final int Gallery_Pick = 1;

    //Variables para cambiar tamaño y resolucion de las imagenes
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private Uri selectedImageUri; //Array de imagenes
    private String Description;
    private ProgressDialog loadingBar;

    //Firebase
    private StorageReference PostsImagesReference;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String current_user_id;
    private String downloadUrl;
    private String TAG = "PublicacionFragment";
    private long contador_posts = 0;

    //vars
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes;
    private double mProgress = 0;


    public PublicacionFragment() {
        // Required empty public constructor
    }

    public interface onPhotoSelectedListener {
        void getImagePath(Uri imagePath);

        void getImageBitmap(Bitmap bitmap);
    }

    onPhotoSelectedListener onPhotoSelectedListener;

    public static PublicacionFragment newInstance() {
        PublicacionFragment fragment = new PublicacionFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicacion, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_publicacion);
        mToolbar.setTitle("Añadir publicación");


        SelectPostImage = (ImageView) view.findViewById(R.id.select_post_image);
        nombre_usuario_new_post = (TextView) view.findViewById(R.id.usuario_new_post);
        imagenPerfilUsuario = (CircleImageView) view.findViewById(R.id.imagen_usuario_new_post);
        btnPublicar = (Button) view.findViewById(R.id.button_publicar);
        extDescripcion = (EditText) view.findViewById(R.id.descripcion_new_post);
        loadingBar = new ProgressDialog(getActivity());

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarPost();

            }
        });


        // Cabezera imagen de usuario y nombre de usuario en Añadir nuevo post...
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("usuario")) {
                        String nombre_usuario = dataSnapshot.child("usuario").getValue().toString();
                        nombre_usuario_new_post.setText(nombre_usuario);
                    }

                    if (dataSnapshot.hasChild("profileimage")) {
                        String imagen = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(imagen).placeholder(R.drawable.profile).into(imagenPerfilUsuario);

                    } else {
                        Toast.makeText(getActivity(), "Sin imagen de perfil... : ", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image uri: " + selectedImageUri);


            //onPhotoSelectedListener.getImagePath(selectedImageUri);
            SelectPostImage.setImageURI(selectedImageUri);

        }
    }

    private void ValidarPost() {
        Description = extDescripcion.getText().toString();
        if (selectedImageUri == null) {
            Toast.makeText(getActivity(), "Por favor selecciona una foto...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(getActivity(), "Escribe algo maravilloso sobre esta foto...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Añadiendo publicacion..");
            loadingBar.setMessage("Por favor espere, mientras se publica tu nueva foto...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage(selectedImageUri);
        }
    }

    private void StoringImageToFirebaseStorage(Uri imagePath) {
        //Metodo para cambiar imagen de tamaño y resolucion
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imagePath);

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());


        postRandomName = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = PostsImagesReference.child("Post Images").child(selectedImageUri.getLastPathSegment() + postRandomName + ".jpg");
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
                    Toast.makeText(getActivity(), "Imagen subida correctamente a Firebase storage...", Toast.LENGTH_SHORT).show();

                    downloadUrl = downUri.toString();
                    GuardarInformacionPublicacion();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(), "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if (bitmap != null) {
                this.mBitmap = bitmap;

            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getActivity(), "compressing image", Toast.LENGTH_SHORT).show();


        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Log.d(TAG, "doInBackground: started.");
            if (mBitmap == null) {
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), params[0]);

                } catch (IOException e) {

                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());

                }

            }

            byte[] bytes = null;
            Log.d(TAG, "doInBackground: megabytes before compression: " + mBitmap.getByteCount() / 1000000);

            bytes = getBytesFromBitmap(mBitmap, 100);
            Log.d(TAG, "doInBackground: megabytes before compression: " + bytes.length / 1000000);

            return bytes;

        }

        @Override
        protected void onPostExecute(byte[] bytes) {

            super.onPostExecute(bytes);
            mUploadBytes = bytes;

            //execute the upload task
        }

    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);


        return stream.toByteArray();

    }

    private void GuardarInformacionPublicacion() {
        PostsRef.addValueEventListener(new ValueEventListener() {
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

            }
        });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("usuario").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();


                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", downloadUrl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("usuario", userName);
                    postsMap.put("contador", contador_posts);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        EnviarUsuarioAPantallaPrincipal();
                                        Toast.makeText(getActivity(), "Su publicacion se ha subido.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Ha ocurrido un error.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void EnviarUsuarioAPantallaPrincipal() {
        Intent irPantallaPrincipal = new Intent(getActivity(), MainActivity.class);
        startActivity(irPantallaPrincipal);

    }


    //Abrir galeria de imagenes para seleccionar foto del usuario
    private void OpenGallery() {
        //Permission Granted, lets go pick photo
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    public void onAttach(Context context) {
        try {
            onPhotoSelectedListener = (onPhotoSelectedListener) getActivity();

        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach : ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }
}
