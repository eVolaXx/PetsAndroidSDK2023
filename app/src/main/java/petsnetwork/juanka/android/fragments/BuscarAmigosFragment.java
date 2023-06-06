package petsnetwork.juanka.android.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import petsnetwork.juanka.android.AmigosClass;
import petsnetwork.juanka.android.PersonProfileActivity;
import petsnetwork.juanka.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuscarAmigosFragment extends Fragment {
    private EditText editTextBuscarAmigos;
    private ImageButton imageButtonBuscarAmigos;
    private RecyclerView RecyclerViewListaAmigos;
    private DatabaseReference UsuariosRef;
    private Toolbar toolbar;


    public BuscarAmigosFragment() {
        // Required empty public constructor
    }

    public static BuscarAmigosFragment newInstance() {
        BuscarAmigosFragment fragment = new BuscarAmigosFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //visitante nueva solicitud
        //String id_visitante_key = getArguments().getString("visitante_key");


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amigos, container, false);


        toolbar = (Toolbar) view.findViewById(R.id.toolbar_amigos);
        toolbar.setTitle("Amigos");

        editTextBuscarAmigos = (EditText) view.findViewById(R.id.editText_buscar_amigos);
        imageButtonBuscarAmigos = (ImageButton) view.findViewById(R.id.imageButton_buscar_amigos);
        RecyclerViewListaAmigos = (RecyclerView) view.findViewById(R.id.buscar_amigos_resultado_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        RecyclerViewListaAmigos.setLayoutManager(linearLayoutManager);


        imageButtonBuscarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String BuscarInputAmigos = editTextBuscarAmigos.getText().toString();
                BuscarAmigosFirebase(BuscarInputAmigos);
            }
        });

        return view;
    }


    private void BuscarAmigosFirebase(final String BuscarInputAmigos) {
        Query searchPeopleFriendsQuery = UsuariosRef.orderByChild("usuario").startAt(BuscarInputAmigos).endAt(BuscarInputAmigos + "\uf8ff");
        FirebaseRecyclerOptions<AmigosClass> options = new FirebaseRecyclerOptions.Builder<AmigosClass>().setQuery(searchPeopleFriendsQuery, AmigosClass.class).build();
        FirebaseRecyclerAdapter<AmigosClass, BuscarAmigosFragment.BuscarAmigosHolder> adapter = new FirebaseRecyclerAdapter<AmigosClass, BuscarAmigosFragment.BuscarAmigosHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BuscarAmigosHolder holder, final int position, @NonNull AmigosClass amigosClass) {
                final String PostKey = getRef(position).getKey();
                holder.usuario.setText(amigosClass.getUsuario());
                holder.mascota.setText("Mascota: "+amigosClass.getMascota());
                Picasso.get().load(amigosClass.getProfileimage()).into(holder.profileimage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /* No funciona /*
                        /*
                        Intent findOthersIntent = new Intent(getActivity(),BuscarAmigosFragment.class);
                        findOthersIntent.putExtra("PostKey", PostKey);
                        startActivity(findOthersIntent);
                        */


                        // Identificador del usuario visitante a otro perfil
                        String visita_user_id = getRef(position).getKey();
                        Intent profileIntent = new Intent(getActivity(), PersonProfileActivity.class);
                        profileIntent.putExtra("visita_user_id", visita_user_id);
                        startActivity(profileIntent);

                    }
                });
            }

            @NonNull
            @Override
            public BuscarAmigosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todos_usuarios_display_layout, parent, false);

                BuscarAmigosFragment.BuscarAmigosHolder viewHolder = new BuscarAmigosFragment.BuscarAmigosHolder(view);
                return viewHolder;
            }
        };

        RecyclerViewListaAmigos.setAdapter(adapter);
        adapter.startListening();


    }

    public class BuscarAmigosHolder extends RecyclerView.ViewHolder {
        TextView usuario, mascota;
        CircleImageView profileimage;

        public BuscarAmigosHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.display_user_amigo);
            mascota = itemView.findViewById(R.id.textView_perro);
            profileimage = itemView.findViewById(R.id.todos_usuarios_profile_image);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsuariosRef = FirebaseDatabase.getInstance().getReference().child("Users");


        setHasOptionsMenu(true);
    }
}
