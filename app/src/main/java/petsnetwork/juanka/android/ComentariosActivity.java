package petsnetwork.juanka.android;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentariosActivity extends AppCompatActivity {
    private RecyclerView comentariosList;
    private ImageButton CommentButon;
    private EditText InputComment;

    private String Post_Key, currentUserID;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);


        Post_Key = getIntent().getExtras().get("PostKey").toString();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comentarios");

        CommentButon = (ImageButton) findViewById(R.id.post_comentario_boton);
        comentariosList = (RecyclerView) findViewById(R.id.comentarios_list);
        InputComment = (EditText) findViewById(R.id.comentarios_input);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comentariosList.setLayoutManager(linearLayoutManager);

        CommentButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String usuario = dataSnapshot.child("usuario").getValue().toString();
                            ValidarComentario(usuario);
                            InputComment.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comentarios> options = new FirebaseRecyclerOptions.Builder<Comentarios>().setQuery(PostsRef, Comentarios.class).build();
        FirebaseRecyclerAdapter<Comentarios, ComentariosActivity.ComentariosViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Comentarios, ComentariosViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ComentariosViewHolder comentariosViewHolder, int i, @NonNull final Comentarios comentarios) {
                        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("profileimage")) {
                                        String imagen_perfil = dataSnapshot.child("profileimage").getValue().toString();

                                        comentariosViewHolder.comentario_username.setText(comentarios.getUsuario());
                                        comentariosViewHolder.comentario_text.setText(comentarios.getComentario());
                                        comentariosViewHolder.comentario_fecha.setText(comentarios.getFecha());
                                        comentariosViewHolder.comentario_hora.setText(comentarios.getTime());
                                        Picasso.get().load(imagen_perfil).into(comentariosViewHolder.circleImageView);
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ComentariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todos_los_comentarios_layout, parent, false);
                        ComentariosActivity.ComentariosViewHolder comentariosViewHolder = new ComentariosActivity.ComentariosViewHolder(view);

                        return comentariosViewHolder;
                    }
                };
        comentariosList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ComentariosViewHolder extends RecyclerView.ViewHolder {

        TextView comentario_username, simple_text, comentario_fecha, comentario_text, comentario_hora;
        CircleImageView circleImageView;

        public ComentariosViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.imagen_usuario_comentario);
            comentario_username = itemView.findViewById(R.id.comentario_username);
            simple_text = itemView.findViewById(R.id.simple_text);
            comentario_fecha = itemView.findViewById(R.id.comentario_fecha);
            comentario_text = itemView.findViewById(R.id.comentario_text);
            comentario_hora = itemView.findViewById(R.id.comentario_hora);

        }
    }

    private void ValidarComentario(String usuario) {
        String comentarioInput = InputComment.getText().toString();

        if (TextUtils.isEmpty(comentarioInput)) {
            Toast.makeText(this, "Por favor, escribe para comentar..", Toast.LENGTH_SHORT).show();
        } else {
            // Firebase
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm");
            final String saveCurrentTime = currentTime.format(calFordTime.getTime());

            final String RandomKey = currentUserID + saveCurrentDate + saveCurrentTime;

            HashMap comentariosMap = new HashMap();
            comentariosMap.put("uid", currentUserID);
            comentariosMap.put("comentario", comentarioInput);
            comentariosMap.put("time", saveCurrentTime);
            comentariosMap.put("fecha", saveCurrentDate);
            comentariosMap.put("usuario", usuario);

            PostsRef.child(RandomKey).updateChildren(comentariosMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ComentariosActivity.this, "Tu comentario se ha enviado..", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ComentariosActivity.this, "Ha ocurrido un error, prueba de nuevo..", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


        }
    }
}
