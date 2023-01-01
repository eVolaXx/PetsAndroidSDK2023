package petsnetwork.juanka.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import petsnetwork.juanka.android.EducationDog;
import petsnetwork.juanka.android.EducationDog;
import petsnetwork.juanka.android.AdoptionActivity;
import petsnetwork.juanka.android.ComentariosActivity;
import petsnetwork.juanka.android.PetsLogin;
import petsnetwork.juanka.android.Posts;
import petsnetwork.juanka.android.R;


public class InicioFragment extends Fragment {
    private DatabaseReference UsersRef, PostsReference, LikesRef;
    private StorageReference UserProfileImageRef;
    String currentUserID;
    Boolean LikeChecked = false;
    private FirebaseAuth mAuth;
    private RecyclerView postList;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private TextView nav_usuario;
    private CircleImageView nav_imagen_perfil;
    private String visitante_key;


    public static InicioFragment newInstance() {
        InicioFragment fragment = new InicioFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        toolbar.setTitle("Pets Social");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //add this line if you want to provide Up Navigation but don't forget to to
        //identify parent activity in manifest file
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);




        // DrawerLayout y navegacion lateral
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,R.string.drawer_open, R.string.drawer_close);   // Boton desplejable menu
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();





        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);

        //Barra de navegacion, nombre de usuario y imagen de perfil
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        nav_imagen_perfil = (de.hdodenhof.circleimageview.CircleImageView) navView.findViewById(R.id.imagen_perfil);
        nav_usuario = (TextView) navView.findViewById(R.id.userName);


        //RecyclerView
        postList = (RecyclerView) view.findViewById(R.id.all_users_post_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        //Nombre de usuario y imagen de perfil barra de navegacion lateral
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    if(dataSnapshot.hasChild("usuario")){
                        String nombre_usuario = dataSnapshot.child("usuario").getValue().toString();
                        nav_usuario.setText(nombre_usuario);
                    }


                    if(dataSnapshot.hasChild("profileimage")) {
                        String imagen = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(imagen).placeholder(R.drawable.profile).into(nav_imagen_perfil);

                    }
                    else {
                        Toast.makeText(getActivity(), "Sin imagen de perfil... : " , Toast.LENGTH_SHORT).show();
                    }





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        MostrarTodasLasPublicaciones();
        return view;
    }
    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.tienda:
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                Intent intentInicio = new Intent(getActivity(), PetsLogin.class);
                startActivity(intentInicio);
                Toast.makeText(getActivity(), "Cerrando sesion", Toast.LENGTH_SHORT).show();
                break;

            case R.id.adopcion:
                Intent AdopcionIntent = new Intent(getActivity(), AdoptionActivity.class);
                startActivity(AdopcionIntent);
                break;
            case R.id.educacion:
                Intent EducacionIntent = new Intent(getActivity(), EducationDog.class);
                startActivity(EducacionIntent);
                break;

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_activity,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == android.R.id.home) {  // Animacion al abrir la barra lateral dando clic al boton
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }


        if (id == R.id.search_bar) {
            Bundle bundle = new Bundle();
            bundle.putString("visitante_key", currentUserID);
            // set Fragmentclass Arguments
            InicioFragment fragmentobj = new InicioFragment();
            fragmentobj.setArguments(bundle);

            //Cambiar al Fragment de buscar amigos
            AppCompatActivity activity = (AppCompatActivity) getContext();
            BuscarAmigosFragment myFragment = new BuscarAmigosFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        PostsReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileimage");


    }

    //Metodo para configurar y aplicar en pantalla todas las publicaciones de los usuarios.
    private void MostrarTodasLasPublicaciones() {
        Query ordenarPublicaciones = PostsReference.orderByChild("contador");
        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(ordenarPublicaciones,Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PublicacionViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PublicacionViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();    // Variable para almacenar el codigo de cada publicaciones y luego recuperarlo en ClickPostActivity y poder editar las publicaciones

                holder.username.setText(model.getUsuario());
                holder.time.setText("" +model.getTime());
                holder.date.setText(""+model.getDate());
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileimage()).into(holder.user_post_image);
                Picasso.get().load(model.getPostimage())
                        .resize(0,1200)
                        .into(holder.postImage);

                holder.setLikesButtonEstado(PostKey);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        Intent ClickPostIntent = new Intent(Inicio.this, ClickPostActivity.class);
                        ClickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(ClickPostIntent);

                         */ // No funciona salta Nullpointer con la descripcion y la foto de Firebase

                    }
                });
                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LikeChecked = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override // Like
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(LikeChecked.equals(true)) {
                                    if (dataSnapshot.child(PostKey).hasChild(currentUserID)) {
                                        LikesRef.child(PostKey).child(currentUserID).removeValue();
                                        LikeChecked = false;

                                        //Dislike
                                    } else {
                                        LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                        LikeChecked = false;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentComment = new Intent(getActivity(), ComentariosActivity.class);
                        intentComment.putExtra("PostKey", PostKey);
                        startActivity(intentComment);

                    }
                });


            }



            @NonNull
            @Override
            public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout,parent,false);
                PublicacionViewHolder viewHolder=new PublicacionViewHolder(view);

                return viewHolder;
            }
        };

        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }









    //Clase generada para crear las vistas de la publicacion y pasarsela a FirebaseRecycler
    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView username,date,time,description,num_likes;
        CircleImageView user_post_image;
        ImageView postImage;
        ImageButton likeButton, commentButton;
        int count_likes;
        String currentIdUsuario;
        DatabaseReference likesRef;

        public PublicacionViewHolder(View itemView) {
            super(itemView);


            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentIdUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

            likeButton = itemView.findViewById(R.id.like_boton);
            commentButton = itemView.findViewById(R.id.comentarios_boton);
            num_likes = itemView.findViewById(R.id.like_numeros);
            username = itemView.findViewById(R.id.post_nombre_usuario);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_descripcion_publicar);
            postImage = itemView.findViewById(R.id.post_imagen);
            user_post_image = itemView.findViewById(R.id.post_profile);

        }

        public void setLikesButtonEstado(final String PostKey){
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentIdUsuario)){
                        count_likes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.likes);
                        num_likes.setText((Integer.toString(count_likes) + (" Likes")));
                    } else {
                        count_likes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.dislike2);
                        num_likes.setText((Integer.toString(count_likes) + (" Likes")));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }





}
