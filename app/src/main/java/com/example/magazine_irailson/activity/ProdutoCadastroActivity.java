package com.example.magazine_irailson.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Produto;
import com.example.magazine_irailson.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class ProdutoCadastroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ExtendedEditText etNomeProduto, etSku, etQuantidade, etValorUnitario;
    private final DatabaseReference referenciaProduto = ConfiguracaoFirebase.getFirebase().child("produtos");

    private Produto produtoEditado;

    private RadioGroup rgUnidadeMedida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_cadastro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_24dp);
        toolbar.setTitle(R.string.salvar_experiencia);
        setSupportActionBar(toolbar);

        onBack(toolbar);

        produtoEditado = (Produto) getIntent().getSerializableExtra("produto");

        etNomeProduto = findViewById(R.id.etNomeProduto);
        etSku = findViewById(R.id.etSku);
        etQuantidade = findViewById(R.id.etQuantidade);
        etValorUnitario = findViewById(R.id.etValorUnitario);
        rgUnidadeMedida = findViewById(R.id.rgUnidadeMedida);

        Button btAlterarExperiencia = findViewById(R.id.btSalvarProduto);
        btAlterarExperiencia.setOnClickListener(v -> validarCampos());

        if (produtoEditado != null)
            carregarProduto(produtoEditado);
    }

    private void carregarProduto(Produto produtoEditado) {
        etNomeProduto.setText(produtoEditado.getNome());
        etSku.setText(produtoEditado.getSku());
        etQuantidade.setText(String.valueOf(produtoEditado.getQuantidadeEstoque()));
        etValorUnitario.setText(String.valueOf(produtoEditado.getValorUnitario()));

        if (produtoEditado.isKg())
            rgUnidadeMedida.check(R.id.rbKG);
        else
            rgUnidadeMedida.check(R.id.rbUNI);
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
        }

        String nome = etNomeProduto.getText().toString();
        String sku = etSku.getText().toString();
        String quantidade = etQuantidade.getText().toString();
        String valorUnitario = etValorUnitario.getText().toString();
        boolean isKG = rgUnidadeMedida.getCheckedRadioButtonId() == R.id.rbKG;
        boolean isUNI = rgUnidadeMedida.getCheckedRadioButtonId() == R.id.rbUNI;


        if (!resultado) {
            if (produtoEditado == null)
                salvarProduto(nome, sku, quantidade, valorUnitario, isKG, isUNI);
            else {
                produtoEditado.setNome(nome);
                produtoEditado.setSku(sku);
                produtoEditado.setQuantidadeEstoque(Double.valueOf(quantidade));
                produtoEditado.setValorUnitario(Double.valueOf(valorUnitario));
                produtoEditado.setKg(isKG);
                produtoEditado.setUnidade(isUNI);

                editarProduto(produtoEditado);
            }
        }
    }

    private void salvarProduto(String nome, String sku, String quantidade, String valorUnitario,
                               boolean isKG, boolean isUNI) {

        try {
            DatabaseReference autoId = referenciaProduto.push();

            Produto produto = new Produto();

            produto.setNome(nome);
            produto.setSku(sku);
            produto.setQuantidadeEstoque(Double.valueOf(Integer.valueOf(quantidade)));
            produto.setValorUnitario(Double.valueOf(valorUnitario));
            produto.setKg(isKG);
            produto.setUnidade(isUNI);

            produto.setId(autoId.getKey());

            salvarFirebase(produto);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarProduto(final Produto produto) {

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

                referenciaProduto.child(produto.getId()).updateChildren(dados);

                Util.mostrarMensagen(ProdutoCadastroActivity.this, "Produto atualizado com sucesso!");
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
            finish();

        } catch (Exception e) {
            Util.mostrarMensagen(this, "Erro ao cadastrar produto");
            e.printStackTrace();
        }
    }
}
