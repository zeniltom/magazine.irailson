package com.example.magazine_irailson.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.activity.ProdutoCadastroActivity;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Produto;
import com.example.magazine_irailson.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class ProdutoRecyclerAdapter extends RecyclerView.Adapter<ProdutoViewHolder> {

    private final ArrayList<Produto> produtos;
    private final Context context;

    private AlertDialog.Builder builder;
    private AlertDialog alert;

    private StorageReference storage = ConfiguracaoFirebase.getFirebaseStorage();
    private StorageReference referenciaProduto = ConfiguracaoFirebase.getFirebaseStorage();
    private DatabaseReference firebase;

    public ProdutoRecyclerAdapter(ArrayList<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    public interface itemClickListenter {

        void onClick(View view, int position, boolean isLongClick);
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto, parent, false);

        return new ProdutoViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProdutoViewHolder holder, final int position) {

        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        moeda.setCurrency(Currency.getInstance(new Locale("pt", "BR")));

        //Setar Propriedades vindas da classe
        final Produto produto = produtos.get(position);

        firebase = ConfiguracaoFirebase.getFirebase().child("produtos").child(produto.getId());
        referenciaProduto = storage.child("fotos").child("produtos")
                .child(produto.getId()).child("foto");

        holder.tvSku.setText(produto.getSku());
        holder.tvNome.setText(produto.getNome());
        holder.tvQtdEstoque.setText(String.valueOf(produto.getQuantidadeEstoque()));
        holder.tvUnidadeMedida.setText(produto.isKg() ? "KG" : "UNI");
        holder.tvValorUnitario.setText(moeda.format(produto.getValorUnitario()));
        carregarImagem(holder.fotoProduto, referenciaProduto);

        holder.setItemClickListener((view, position1, isLongClick) -> {
            Intent intent = new Intent(context, ProdutoCadastroActivity.class);
            intent.putExtra("produto", produto);
            context.startActivity(intent);
        });

        holder.btExcluirProduto.setOnClickListener(v -> {

            builder = new AlertDialog.Builder(context);
            builder.setTitle("Alerta");
            builder.setMessage("Deseja Deletar este produto ''" + produto.getNome() + "''?");
            builder.setCancelable(true);
            builder.setIcon(R.mipmap.ic_launcher);

            builder.setPositiveButton("SIM", (dialog, i) -> deletarProduto(firebase, produto));
            builder.setNegativeButton("NÃO", (dialog, i) -> dialog.cancel());

            alert = builder.create();
            alert.show();
        });

        holder.btDetalhesProduo.setOnClickListener(v -> Util.mostrarMensagen(context, "Ver detalhes " + produtos.get(position).getNome()));
    }

    private void carregarImagem(@NonNull ImageView holder, StorageReference referenciaProduto) {
        long ONE_MEGABYTE = 1024 * 1024;
        referenciaProduto.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            if (bytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.setImageBitmap(bitmap);
            } else
                holder.setImageResource(R.drawable.padrao);

        });
    }

    private void deletarProduto(DatabaseReference firebase, Produto produto) {
        firebase.removeValue((databaseError, databaseReference) -> Util.mostrarMensagen(context, "Deletando " + produto.getNome()));
        referenciaProduto.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Util.mostrarMensagen(context, "Erro ao apagar foto!");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (produtos != null) return produtos.size();

        else return 0;
    }
}
