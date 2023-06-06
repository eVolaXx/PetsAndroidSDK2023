package petsnetwork.juanka.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoPersonActivity extends AppCompatActivity {
    TextView email, usuario, raza, mascotas;
    Button editarInfoUsuario;
    private DatabaseReference InfoRefUserFirebase;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_person);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        InfoRefUserFirebase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        usuario = (TextView) findViewById(R.id.nombre_usuario_perfil_info);
        mascotas = (TextView) findViewById(R.id.mascota_info);
        raza = (TextView) findViewById(R.id.raza_perfil_info);
        email = (TextView) findViewById(R.id.email_info);
        editarInfoUsuario = (Button) findViewById(R.id.button_editar_info_usuario);


        InfoRefUserFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myUsuario = dataSnapshot.child("usuario").getValue().toString();
                    String myMascotas = dataSnapshot.child("mascota").getValue().toString();
                    String raza_mascota = dataSnapshot.child("raza").getValue().toString();
                    String myEmail = mAuth.getCurrentUser().getEmail();

                    usuario.setText(myUsuario);
                    mascotas.setText(myMascotas);
                    raza.setText(raza_mascota);
                    email.setText(myEmail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editarInfoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar informacion de usuario
            }
        });


    }
}
