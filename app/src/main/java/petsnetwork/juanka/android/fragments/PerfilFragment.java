package petsnetwork.juanka.android.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import petsnetwork.juanka.android.InfoPersonActivity;
import petsnetwork.juanka.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    private ImageView imageView, settings_perfil;
    private TextView nombre_usuario, ciudad, numero_de_seguidores;
    private DatabaseReference perfilRefFirebase, AmigosRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private int numero_amigos = 0;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        imageView = (ImageView) view.findViewById(R.id.perfil_imagen_usuario);
        settings_perfil = (ImageView) view.findViewById(R.id.settings_perfil);
        nombre_usuario = (TextView) view.findViewById(R.id.tv_name);
        ciudad = (TextView) view.findViewById(R.id.address_request);
        numero_de_seguidores = (TextView) view.findViewById(R.id.numero_seguidores);


        perfilRefFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsuario = dataSnapshot.child("usuario").getValue().toString();

                    String myCiudad = dataSnapshot.child("ciudad").getValue().toString();


                    Picasso.get().load(myProfileImage).into(imageView);
                    nombre_usuario.setText("@" + myUsuario);
                    ciudad.setText(myCiudad);


                    AmigosRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                numero_amigos = (int) dataSnapshot.getChildrenCount();
                                numero_de_seguidores.setText(Integer.toString(numero_amigos));
                            } else {
                                numero_de_seguidores.setText("O seguidores");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settings_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambiar al Fragment de informacion de perfil
                Intent intent = new Intent(getActivity(), InfoPersonActivity.class);
                startActivity(intent);


            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        perfilRefFirebase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        AmigosRef = FirebaseDatabase.getInstance().getReference().child("Amigos");


    }
}
