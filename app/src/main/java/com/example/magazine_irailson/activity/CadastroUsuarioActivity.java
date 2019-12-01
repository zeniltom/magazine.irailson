package com.example.magazine_irailson.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Usuario;
import com.example.magazine_irailson.util.Util;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private ExtendedEditText editNomeUsuario;
    private ExtendedEditText editEmailUsuario;
    private ExtendedEditText editSenhaUsuario;
    private Button btCadastrar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_24dp);
        toolbar.setTitle(getString(R.string.cadastrar_usuario));
        setSupportActionBar(toolbar);

        editNomeUsuario = findViewById(R.id.edit_nome_usuario);
        editEmailUsuario = findViewById(R.id.edit_email_usuario);
        editSenhaUsuario = findViewById(R.id.edit_senha_usuario);

        btCadastrar = findViewById(R.id.bt_cadastrar);
        btCadastrar.setOnClickListener(view -> validarCampos());
    }

    private void validarCampos() {
        boolean resultado;

        if (resultado = Util.isCampoVazio(editNomeUsuario.getText().toString())) {
            editNomeUsuario.setError(getString(R.string.preencha_nome));
            editNomeUsuario.requestFocus();

        } else if (resultado = Util.isCampoVazio(editEmailUsuario.getText().toString())) {
            editEmailUsuario.setError(getString(R.string.preencha_email));
            editEmailUsuario.requestFocus();

        } else if (resultado = Util.isCampoVazio(editSenhaUsuario.getText().toString()) ||
                (editSenhaUsuario.getText().toString().length() < 8)) {

            editSenhaUsuario.setError(getString(R.string.preencha_os_dados_senha));
            editSenhaUsuario.requestFocus();
        }

        String nome = editNomeUsuario.getText().toString();
        String email = editEmailUsuario.getText().toString();
        String senha = editSenhaUsuario.getText().toString();

        if (!resultado) cadastrarUsuario(nome, email, senha);
    }

    private void cadastrarUsuario(final String nome, final String email, final String senha) {
        progressDialog = Util.mostrarProgressDialog(this, "Cadastrando...");

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                email,
                senha
        ).addOnCompleteListener(CadastroUsuarioActivity.this, task -> {

            if (task.isSuccessful()) {

                FirebaseUser usuarioFirebase = task.getResult().getUser();

                String identificadorUsuario = usuarioFirebase.getUid();

                Usuario usuario = new Usuario();
                usuario.setId(identificadorUsuario);
                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setSenha(senha);

                //Salvar no Firebase
                DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
                referenciaFirebase.child("usuarios").child(identificadorUsuario).setValue(usuario);


                Util.mostrarMensagen(
                        CadastroUsuarioActivity.this, "Sucesso ao cadastrar!");
                progressDialog.dismiss();
                abrirLogin();

            } else {
                progressDialog.dismiss();
                String erroExcecao;

                try {
                    throw Objects.requireNonNull(task.getException());

                } catch (FirebaseAuthWeakPasswordException e) {
                    erroExcecao = "A Senha deve conter carácteres, letras e números!";

                } catch (FirebaseAuthInvalidCredentialsException e) {
                    erroExcecao = "O E-mail digitado é inválido, digite um novo E-mail!";

                } catch (FirebaseAuthUserCollisionException e) {
                    erroExcecao = "Esse E-mail já está em uso!";

                } catch (FirebaseNetworkException e) {
                    erroExcecao = "Não há conexão com a internet!";

                } catch (Exception e) {
                    erroExcecao = "Ao efetuar o cadastro!";
                    e.printStackTrace();
                }

                Util.mostrarMensagen(
                        CadastroUsuarioActivity.this, "Erro: " + erroExcecao);
            }
        });
    }

    private void abrirLogin() {
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
