package com.example.magazine_irailson.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Produto;
import com.example.magazine_irailson.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class ProdutoCadastroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ExtendedEditText etNomeProduto, etSku, etQuantidade, etValorUnitario;
    private final DatabaseReference referenciaProduto = ConfiguracaoFirebase.getFirebase().child("produtos");

    private Produto produto;

    private RadioGroup rgUnidadeMedida;
    private ProgressBar progressBar;

    private StorageReference storage = ConfiguracaoFirebase.getFirebaseStorage();
    private String foto;
    private ImageView fotoPostagem;
    private String urlConvertida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_cadastro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_24dp);
        toolbar.setTitle(R.string.salvar_experiencia);
        setSupportActionBar(toolbar);

        onBack(toolbar);

        produto = (Produto) getIntent().getSerializableExtra("produto");

        etNomeProduto = findViewById(R.id.etNomeProduto);
        etSku = findViewById(R.id.etSku);
        etQuantidade = findViewById(R.id.etQuantidade);
        etValorUnitario = findViewById(R.id.etValorUnitario);
        rgUnidadeMedida = findViewById(R.id.rgUnidadeMedida);
        fotoPostagem = findViewById(R.id.fotoPostagem);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Button btAlterarExperiencia = findViewById(R.id.btSalvarProduto);
        btAlterarExperiencia.setOnClickListener(v -> validarCampos());

        if (produto != null)
            carregarProduto(produto);
    }

    private void carregarProduto(Produto produtoEditado) {
        etNomeProduto.setText(produtoEditado.getNome());
        etSku.setText(produtoEditado.getSku());
        etQuantidade.setText(String.valueOf(produtoEditado.getQuantidadeEstoque()));
        etValorUnitario.setText(String.valueOf(produtoEditado.getValorUnitario()));

        carregarImagem(produtoEditado);

        foto = produtoEditado.getFoto();

        if (produtoEditado.isKg())
            rgUnidadeMedida.check(R.id.rbKG);
        else
            rgUnidadeMedida.check(R.id.rbUNI);
    }

    private void carregarImagem(Produto produtoEditado) {
        if (produtoEditado.getFoto() != null) {

            StorageReference referenciaProdutoStorage = storage.child("fotos").child("produtos")
                    .child(produto.getId()).child("foto");

            long ONE_MEGABYTE = 1024 * 1024;
            referenciaProdutoStorage.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if (bytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    fotoPostagem.setImageBitmap(bitmap);
                } else
                    fotoPostagem.setImageResource(R.drawable.padrao);
            });
        }
    }

    private void onBack(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void validarCampos() {
        boolean resultado = false;

        if (Util.isCampoVazio(etNomeProduto.getText().toString())) {
            etNomeProduto.setError("Preencha o nome do produto");
            etNomeProduto.requestFocus();
            return;

        } else if (Util.isCampoVazio(etSku.getText().toString())) {
            etSku.setError("Preencha o SKU do produto");
            etSku.requestFocus();
            return;

        } else if (Util.isCampoVazio(etQuantidade.getText().toString())) {
            etQuantidade.setError("Preencha a quantidade do produto");
            etQuantidade.requestFocus();
            return;

        } else if (Util.isCampoVazio(etValorUnitario.getText().toString())) {
            etValorUnitario.setError("Preencha o valor unitário");
            etValorUnitario.requestFocus();
        } else if (rgUnidadeMedida.getCheckedRadioButtonId() == -1) {
            Util.mostrarInfoDialog(this, "Escolha a unidade de medida!");
            rgUnidadeMedida.requestFocus();
            return;
        } else if (foto == null) {
            Util.mostrarInfoDialog(this, "Escolha a foto do produto!");
            fotoPostagem.requestFocus();
            return;
        }

        String nome = etNomeProduto.getText().toString();
        String sku = etSku.getText().toString();
        String quantidade = etQuantidade.getText().toString();
        String valorUnitario = etValorUnitario.getText().toString();
        boolean isKG = rgUnidadeMedida.getCheckedRadioButtonId() == R.id.rbKG;
        boolean isUNI = rgUnidadeMedida.getCheckedRadioButtonId() == R.id.rbUNI;

        if (!resultado) {
            if (produto == null)
                salvarProduto(nome, sku, quantidade, valorUnitario, isKG, isUNI);
            else {
                produto.setNome(nome);
                produto.setSku(sku);
                produto.setQuantidadeEstoque(Double.valueOf(quantidade));
                produto.setValorUnitario(Double.valueOf(valorUnitario));
                produto.setKg(isKG);
                produto.setUnidade(isUNI);

                editarProduto();
            }
        }
    }

    private void salvarProduto(String nome, String sku, String quantidade, String valorUnitario,
                               boolean isKG, boolean isUNI) {
        progressBar.setVisibility(View.VISIBLE);

        try {
            DatabaseReference autoId = referenciaProduto.push();
            produto = new Produto();

            produto.setNome(nome);
            produto.setSku(sku);
            produto.setQuantidadeEstoque(Double.valueOf(Integer.valueOf(quantidade)));
            produto.setValorUnitario(Double.valueOf(valorUnitario));
            produto.setKg(isKG);
            produto.setUnidade(isUNI);

            produto.setId(autoId.getKey());

            //Salva a imagem do produto no Firebase Storage
            StorageReference referenciaProduto = storage.child("fotos").child("produtos").child(produto.getId()).child("foto");

            //Faz upload da foto
            UploadTask uploadTask = referenciaProduto.putFile(Uri.parse(foto));
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                urlConvertida = Objects.requireNonNull(firebaseUrl).toString();
                produto.setFoto(urlConvertida);
                salvarFirebase(produto);

            }).addOnFailureListener(e -> {
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarProduto() {
        progressBar.setVisibility(View.VISIBLE);

        if (foto != null)
            salvarImagem();

        referenciaProduto.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Object> dados = new HashMap<>();
                dados.put("nome", produto.getNome());
                dados.put("sku", produto.getSku());
                dados.put("quantidadeEstoque", produto.getQuantidadeEstoque());
                dados.put("valorUnitario", produto.getValorUnitario());
                dados.put("kg", produto.isKg());
                dados.put("unidade", produto.isUnidade());
                dados.put("foto", produto.getFoto());

                referenciaProduto.child(produto.getId()).updateChildren(dados);

                Util.mostrarMensagen(ProdutoCadastroActivity.this, "Produto atualizado com sucesso!");
                progressBar.setVisibility(View.GONE);

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void salvarFirebase(Produto produto) {
        try {
            referenciaProduto.child(produto.getId()).setValue(produto);

            Util.mostrarMensagen(this, "Sucesso ao cadastrar produto");
            progressBar.setVisibility(View.GONE);

            finish();

        } catch (Exception e) {
            Util.mostrarMensagen(this, "Erro ao cadastrar produto");
            e.printStackTrace();
        }
    }

    private void salvarImagem() {
        if (produto != null && produto.getId() != null) {
            //Atualiza a imagem do produto no Firebase Storage
            StorageReference referenciaProduto = storage.child("fotos").child("produtos").child(produto.getId()).child("foto");

            //Faz upload da foto
            UploadTask uploadTask = referenciaProduto.putFile(Uri.parse(foto));
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                urlConvertida = Objects.requireNonNull(firebaseUrl).toString();
                produto.setFoto(urlConvertida);

            }).addOnFailureListener(e -> {
            });
        }
    }

    // Método para selecionar foto
    public void selecionarFoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Recuperar imagem
            Uri imagemSelecionada = Objects.requireNonNull(data).getData();
            foto = Objects.requireNonNull(imagemSelecionada).toString();

            //Inserir imagem no ImageView
            fotoPostagem.setImageURI(imagemSelecionada);
            salvarImagem();
        }
    }

    //Méotodo que verifica se o usuário já liberou permissões para o App
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }// Método que solicita ao usuário a liberação de permissões para App

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialogInterface, i) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
