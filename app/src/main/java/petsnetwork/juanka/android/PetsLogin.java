package petsnetwork.juanka.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Clase para iniciar sesion en la aplicacion
public class PetsLogin extends AppCompatActivity {
    protected EditText textEmail;
    protected EditText textpass;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAutentificacion;
    private FirebaseAuth.AuthStateListener listenerFirebase;
    //private ImageView googleSign;
    //private static final int RC_SIGN_IN = 1;
    // private GoogleApiClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_login);

        //Firebase
        mAutentificacion = FirebaseAuth.getInstance();


        textEmail = (EditText) findViewById(R.id.editText_email);
        textpass = (EditText) findViewById(R.id.contraseña);
        //googleSign = (ImageView) findViewById(R.id.google_sign);
        loadingBar = new ProgressDialog(this);

        /*
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAutentificacion.getCurrentUser();
        if (currentUser != null) {
            EnviarUsuarioAInicio();
        }
    }


    //Metodo boton iniciar sesion
    public void iniciarSesion(View view) {
        ingresarEnFirebase();

    }

    private void ingresarEnFirebase() {
        String email = textEmail.getText().toString();
        String password = textpass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Por favor escriba su email..", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Por favor escriba su contraseña..", Toast.LENGTH_SHORT).show();

        } else {
            loadingBar.setTitle("Iniciando sesion");
            loadingBar.setMessage("Por favor espere, mientras se inicia sesion...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAutentificacion.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        EnviarUsuarioAInicio();
                        Toast.makeText(getApplicationContext(), "Has iniciado sesión correctamente ", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String mensaje = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Error con la cuenta " + mensaje, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();


                    }

                }
            });
        }
    }

    private void EnviarUsuarioAInicio() {
        Intent HomeIntent = new Intent(PetsLogin.this, MainActivity.class);
        HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(HomeIntent);
        finish();
    }

    //Textview crear cuenta, se lanza la actividad de CrearCuenta para crear nuevo usuario en Firebase
    public void CrearCuenta(View view) {
        Intent myIntent = new Intent(PetsLogin.this, CrearCuenta.class);
        PetsLogin.this.startActivity(myIntent);
        finish();

    }


}
