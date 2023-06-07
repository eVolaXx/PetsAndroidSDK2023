package petsnetwork.juanka.android;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//Clase que se lanzara cuando en buscando amigos queramos agregar a un nuevo amigo. Saldra toda la informacion de ese usuario y para poder mandarle la solicitud de amistad.
public class PersonProfileActivity extends AppCompatActivity {
    private ImageView imagenUsuario;
    private TextView nombre_usuario,ciudad,seguidores;
    private Button cancelarSolicitudAmistad;
    private Button enviarSolicitudAmistad;
    private DatabaseReference AmigosRequestRef,UsersRef, AmigosRef;
    private FirebaseAuth mAuth;
    private String senderUserID,receiverUserID,Estado_actual, saveCurrentDate;
    private int numero_amigos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);
        inicializarViews();

        mAuth = FirebaseAuth.getInstance();


        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = getIntent().getExtras().get("visita_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        AmigosRef = FirebaseDatabase.getInstance().getReference().child("Amigos");
        AmigosRequestRef = FirebaseDatabase.getInstance().getReference().child("AmigosRequest");
        seguidores = (TextView) findViewById(R.id.seguidores_request);

        UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsuario= dataSnapshot.child("usuario").getValue().toString();
                    String myCiudad = dataSnapshot.child("ciudad").getValue().toString();

                    Picasso.get().load(myProfileImage).into(imagenUsuario);
                    nombre_usuario.setText("@" +myUsuario);
                    ciudad.setText(myCiudad);

                    MainOfButtons();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Solicitud de amistad
        cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
        cancelarSolicitudAmistad.setEnabled(false);

        // Verificar usuarios para saber si son amigos o no en la red social y habilitar botones para mandar solicitud
        if(!senderUserID.equals(receiverUserID)){
            enviarSolicitudAmistad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enviarSolicitudAmistad.setEnabled(false);

                    if(Estado_actual.equals("no_amigos")){
                        NuevaSolicitudAmistad();
                    }

                    if(Estado_actual.equals("request_sent")){
                        CancelarSolicitudAmistad();
                    }
                    if(Estado_actual.equals("request_recibido")){
                        AceptarSolicitudAmistad();
                    }
                    if(Estado_actual.equals("amigos")){
                        UnFriendMethod();
                    }
                }
            });

        } else {
            cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
            enviarSolicitudAmistad.setVisibility(View.INVISIBLE);
        }

        //Numero de seguidores
        AmigosRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    numero_amigos = (int) dataSnapshot.getChildrenCount();
                    seguidores.setText(Integer.toString(numero_amigos));
                } else {
                    seguidores.setText("O seguidores");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void UnFriendMethod() {
        AmigosRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    AmigosRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                enviarSolicitudAmistad.setEnabled(true);
                                Estado_actual = "no_amigos";
                                enviarSolicitudAmistad.setText("Enviar solicitud");
                                enviarSolicitudAmistad.setBackgroundColor(Color.GREEN);


                                cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
                                cancelarSolicitudAmistad.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void AceptarSolicitudAmistad() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        AmigosRef.child(senderUserID).child(receiverUserID).child("fecha").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            AmigosRef.child(receiverUserID).child(senderUserID).child("fecha").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                AmigosRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            AmigosRequestRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        enviarSolicitudAmistad.setEnabled(true);
                                                                        Estado_actual = "amigos";
                                                                        enviarSolicitudAmistad.setText("Dejar de seguir");
                                                                        enviarSolicitudAmistad.setBackgroundColor(Color.GREEN);


                                                                        cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
                                                                        cancelarSolicitudAmistad.setEnabled(false);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            }

                                        }
                                    });
                        }

                    }
                });





    }

    private void CancelarSolicitudAmistad() {
        AmigosRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    AmigosRequestRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                enviarSolicitudAmistad.setEnabled(true);
                                Estado_actual = "no_amigos";
                                enviarSolicitudAmistad.setText("Enviar solicitud");
                                enviarSolicitudAmistad.setBackgroundColor(Color.GREEN);


                                cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
                                cancelarSolicitudAmistad.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void MainOfButtons() {

        AmigosRequestRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receiverUserID)){
                    String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                    if(request_type.equals("sent")){
                        Estado_actual = "request_sent";
                        enviarSolicitudAmistad.setText("Dejar de seguir");
                        enviarSolicitudAmistad.setBackgroundColor(Color.RED);

                        cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
                        cancelarSolicitudAmistad.setEnabled(false);

                    } else if(request_type.equals("recibido")) {
                        Estado_actual = "request_recibido";
                        enviarSolicitudAmistad.setText("Aceptar solicitud");

                        cancelarSolicitudAmistad.setVisibility(View.VISIBLE);
                        cancelarSolicitudAmistad.setEnabled(true);

                        cancelarSolicitudAmistad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelarSolicitudAmistad();
                            }
                        });


                    } else {
                        AmigosRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(receiverUserID)){
                                    Estado_actual = "amigos";
                                    enviarSolicitudAmistad.setText("Dejar de seguir");
                                    cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
                                    cancelarSolicitudAmistad.setEnabled(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void NuevaSolicitudAmistad() {
        AmigosRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    AmigosRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("recibido").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                enviarSolicitudAmistad.setEnabled(true);
                                Estado_actual = "request_sent";
                                enviarSolicitudAmistad.setText("Dejar de seguir");

                                cancelarSolicitudAmistad.setVisibility(View.INVISIBLE);
                                cancelarSolicitudAmistad.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void inicializarViews(){
        imagenUsuario = (ImageView) findViewById(R.id.perfil_imagen_usuario_request);
        nombre_usuario = (TextView) findViewById(R.id.tv_name_request);
        ciudad = (TextView) findViewById(R.id.address_request);
        enviarSolicitudAmistad = (Button) findViewById(R.id.enviar_solicitud_amistad);
        cancelarSolicitudAmistad = (Button) findViewById(R.id.cancelar_solicitud_amistad);

        Estado_actual = "no_amigos";

    }
}
