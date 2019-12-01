package com.example.magazine_irailson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.magazine_irailson.R;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private ProgressBar progressBarLogin;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Cria referência para elementos
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoEntrar = findViewById(R.id.buttonLogin);
        progressBarLogin = findViewById(R.id.progressLogin);

        progressBarLogin.setVisibility(View.GONE); // Oculta o ProgressBar
        verificarUsuarioLogado(); // Chamada do método para verificar se o usuário está
        // autenticado, se estiver o usuário é enviado para MainActivity;

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                // Valida se o e-mail e senha foram preenchidos
                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {
                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin(usuario);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para verificar se o usuário está autenticado. Se estiver loga já envio o mesmo para
    // a listagem de postagens
    private void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (autenticacao.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void validarLogin(Usuario usuario) {
        progressBarLogin.setVisibility(View.VISIBLE); // Deixa visível a ProgressBar

        // Cria referência para o Firebase Autenticação usando a class ConfiguraçãoFirebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        /// Utiliza o método de Autenticação do Firebase
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarLogin.setVisibility(View.GONE); // Oculta o ProgressBar
                if (task.isSuccessful()) {
                    // Caso o login tenha ocorrido com sucesso, o usuário é enviado para a
                    // Activity Principal
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // Verificação de exeções. Verificação padrão do Firebase
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    // Toast com mensagem de erro
                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para abrir a Activity Cadastrar Usuário
    public void abrirCadastro(View view) {
        startActivity(new Intent(this, CadastroUsuarioActivity.class));
    }
}
