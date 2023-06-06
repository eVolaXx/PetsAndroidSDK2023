package petsnetwork.juanka.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Clase para crear nueva cuenta de usuario en la aplicacion.
public class CrearCuenta extends AppCompatActivity {
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button BotonCrearCuenta;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        mAuth = FirebaseAuth.getInstance();


        UserEmail = (EditText) findViewById(R.id.registrar_email);
        UserPassword = (EditText) findViewById(R.id.registrar_contraseña);
        UserConfirmPassword = (EditText) findViewById(R.id.editText_confirmarContraseña);
        BotonCrearCuenta = (Button) findViewById(R.id.button_crear_cuenta);
        loadingBar = new ProgressDialog(this);


        BotonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            EnviarUsuarioAInicio();
        }
    }

    private void EnviarUsuarioAInicio() {
        Intent HomeIntent = new Intent(CrearCuenta.this, MainActivity.class);
        HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(HomeIntent);
        finish();
    }

    private void CreateNewAccount() {
        //String nombre_usuario = NombreUsuario.getText().toString();
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor escriba su email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor escriba su contraseña...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Por favor confirma su contraseña...", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Tu contraseña no coincide...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creando nueva cuenta");
            loadingBar.setMessage("Por favor espere, mientras se crea su nueva cuenta...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                EnviarUsuarioASetudActivity();
                                Toast.makeText(CrearCuenta.this, "Has iniciado correctamente...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(CrearCuenta.this, "Ha ocurrido un error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }

    // No funciona aun esta actividad de rellenar mas datos para el usuario
    private void EnviarUsuarioASetudActivity() {
        Intent setupIntent = new Intent(CrearCuenta.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }


}
