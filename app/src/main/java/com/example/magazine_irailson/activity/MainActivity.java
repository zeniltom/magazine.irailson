package com.example.magazine_irailson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;

    private FragmentManager fragmentManager;
    private NavigationView navigationView;

    @Override
    protected void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerUsuario);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerUsuario);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabCadastrarProduto = findViewById(R.id.fabCadastrarProduto);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("usuarios")
                .child(Objects.requireNonNull(usuarioFirebase.getUid()));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        View headerView = navigationView.getHeaderView(0);

        carregarDadosUsuario(headerView);

        if (navigationView.getMenu().getItem(0).isChecked())
            fragmentManager.beginTransaction().replace(R.id.content, new ProdutoFragment()).commit();

        fabCadastrarProduto.setOnClickListener(v -> abrirCadastroProduto());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                fragmentManager.beginTransaction().replace(R.id.content, new ProdutoFragment()).commit();
                break;
            case R.id.nav_sobre:
                fragmentManager.beginTransaction().replace(R.id.content, new SobreFragment()).commit();
                break;
            case R.id.nav_contatos:
                fragmentManager.beginTransaction().replace(R.id.content, new ContatosFragment()).commit();
                break;
            case R.id.nav_share:
                fragmentManager.beginTransaction().replace(R.id.content, new CompartilharFragment()).commit();
                break;
            case R.id.nav_sair:
                sair();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void carregarDadosUsuario(final View headerView) {

        valueEventListenerUsuario = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                if (usuario != null) {

                    TextView nome = headerView.findViewById(R.id.nav_header_nome_usuario);
                    TextView email = headerView.findViewById(R.id.nav_header_email_usuario);
                    //imagemPerfil = headerView.findViewById(R.id.nav_imagem_perfil);

                    nome.setText(usuario.getNome());
                    email.setText(usuario.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void abrirCadastroProduto() {
        Intent i = new Intent(this, ProdutoCadastroActivity.class);
        startActivity(i);
    }

    private void sair() {

        usuarioFirebase.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
