package petsnetwork.juanka.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

// Clase para filtrar perro en adopcion buscando por localidad
public class SearchDogActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference AdoptionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dog);

        //FiltrarPerroPorLocalidad();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        AdoptionReference = FirebaseDatabase.getInstance().getReference().child("Adopciones");
    }

    private void FiltrarPerroPorLocalidad() {
        Query dog_location = AdoptionReference.child("Adopciones").orderByChild("dog_location");

        FirebaseRecyclerOptions<PerrosAdopcionInfo> options = new FirebaseRecyclerOptions.Builder<PerrosAdopcionInfo>().setQuery(dog_location, PerrosAdopcionInfo.class).build();
        FirebaseRecyclerAdapter<PerrosAdopcionInfo, AdoptionActivity.AdopcionesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PerrosAdopcionInfo, AdoptionActivity.AdopcionesViewHolder>(options) {
            @NonNull
            @Override
            public AdoptionActivity.AdopcionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull AdoptionActivity.AdopcionesViewHolder adopcionesViewHolder, int i, @NonNull PerrosAdopcionInfo perrosAdopcionInfo) {

            }
        };
    }
}