package petsnetwork.juanka.android.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import petsnetwork.juanka.android.AmigosClass;
import petsnetwork.juanka.android.PersonProfileActivity;
import petsnetwork.juanka.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaAmigosFragment extends Fragment {
    private RecyclerView FriendsList;
    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth auth;
    private String online_user_ID;
    private Toolbar toolbar;


    public ListaAmigosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // firebase
        auth = FirebaseAuth.getInstance();
        online_user_ID = auth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Amigos").child(online_user_ID);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_amigos, container, false);

        FriendsList = (RecyclerView) view.findViewById(R.id.friendsList);
        FriendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        FriendsList.setLayoutManager(linearLayoutManager);


        toolbar = (Toolbar) view.findViewById(R.id.amigos_bar);
        toolbar.setTitle("Mis Amigos");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //add this line if you want to provide Up Navigation but don't forget to to
        //identify parent activity in manifest file
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MostrarTodosLosAmigos();


        return view;
    }

    private void MostrarTodosLosAmigos() {
        FirebaseRecyclerOptions<AmigosClass> options = new FirebaseRecyclerOptions.Builder<AmigosClass>().setQuery(FriendsRef, AmigosClass.class).build();
        FirebaseRecyclerAdapter<AmigosClass, ListaAmigosFragment.AmigosViewHolder> adapter = new FirebaseRecyclerAdapter<AmigosClass, AmigosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AmigosViewHolder amigosViewHolder, final int i, @NonNull final AmigosClass amigosModel) {
                final String usersIDS = getRef(i).getKey();

                UsersRef.child(usersIDS).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String PostKey = getRef(i).getKey();

                            amigosViewHolder.usuario.setText(amigosModel.getUsuario());
                            Picasso.get().load(amigosModel.getProfileimage())
                                    .resize(0,1200)
                                    .into(amigosViewHolder.user_image);



                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                      databaseError.getMessage();
                    }
                });

                amigosViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /* No funciona /*
                        /*
                        Intent findOthersIntent = new Intent(getActivity(),BuscarAmigosFragment.class);
                        findOthersIntent.putExtra("PostKey", PostKey);
                        startActivity(findOthersIntent);
                        */


                        // Identificador del usuario visitante a otro perfil
                        String visita_user_id = getRef(i).getKey();
                        Intent profileIntent = new Intent(getActivity(), PersonProfileActivity.class);
                        profileIntent.putExtra("visita_user_id", visita_user_id);
                        startActivity(profileIntent);

                    }
                });
            }


            @NonNull
            @Override
            public AmigosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todos_usuarios_display_layout, parent, false);

                AmigosViewHolder viewHolder = new AmigosViewHolder(view);
                return viewHolder;
            }
        };


        FriendsList.setAdapter(adapter);
        adapter.startListening();


    }

    public class AmigosViewHolder extends RecyclerView.ViewHolder {
        TextView usuario;
        CircleImageView user_image;

        public AmigosViewHolder(@NonNull View itemView) {
            super(itemView);

            usuario = itemView.findViewById(R.id.display_user_amigo);
            user_image = itemView.findViewById(R.id.todos_usuarios_profile_image);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        online_user_ID = auth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Amigos").child(online_user_ID);

    }
}
