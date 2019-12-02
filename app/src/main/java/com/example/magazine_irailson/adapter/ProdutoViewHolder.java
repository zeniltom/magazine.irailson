package com.example.magazine_irailson.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.magazine_irailson.R;

public class ProdutoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    final TextView tvSku;
    final TextView tvNome;
    final TextView tvQtdEstoque;
    final TextView tvValorUnitario;
    final TextView tvUnidadeMedida;
    final Button btExcluirProduto;
    final Button btDetalhesProduo;
    ImageView fotoProduto;

    private ProdutoRecyclerAdapter.itemClickListenter itemClickListener;

    ProdutoViewHolder(View itemView) {
        super(itemView);

        tvNome = itemView.findViewById(R.id.tvNome);
        tvSku = itemView.findViewById(R.id.tvSku);
        tvQtdEstoque = itemView.findViewById(R.id.tvQtdEstoque);
        tvValorUnitario = itemView.findViewById(R.id.tvValorUnitario);
        tvUnidadeMedida = itemView.findViewById(R.id.tvUnidadeMedida);
        btExcluirProduto = itemView.findViewById(R.id.btExcluirProduto);
        btDetalhesProduo = itemView.findViewById(R.id.btDetalhesProduo);
        fotoProduto = itemView.findViewById(R.id.fotoProduto);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    void setItemClickListener(ProdutoRecyclerAdapter.itemClickListenter itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), true);
        return true;
    }
}
