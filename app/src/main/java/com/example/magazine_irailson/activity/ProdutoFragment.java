package com.example.magazine_irailson.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.adapter.ProdutoRecyclerAdapter;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.example.magazine_irailson.model.Produto;
import com.example.magazine_irailson.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ProdutoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ProdutoRecyclerAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ConstraintLayout contentEmpty;

    private ArrayList<Produto> produtos;
    private boolean hasConnection;

    private Query firebase;
    private ValueEventListener valueEventListenerProdutos;

    public ProdutoFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerProdutos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerProdutos);
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarProdutos();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_produto, container, false);

        mRecyclerView = view.findViewById(R.id.recycleViewProduto);
        mSwipeRefreshLayout = view.findViewById(R.id.srlProduto);
        contentEmpty = view.findViewById(R.id.content_empty);

        hasConnection = Util.verifyConnection(Objects.requireNonNull(getActivity()));
        produtos = new ArrayList<>();

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("produtos");

        carregarProdutos();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new ProdutoRecyclerAdapter(produtos, getContext());
        mRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this::carregarProdutos);

        return view;
    }

    private void carregarProdutos() {

        if (hasConnection) {
            mSwipeRefreshLayout.setRefreshing(true);

            valueEventListenerProdutos = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Limpar lista
                    produtos.clear();

                    //Listar produtos
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Produto produto = dados.getValue(Produto.class);
                        produtos.add(produto);
                    }

                    //Se não possui produtos, o conteudo some
                    if (produtos.size() > 0) {
                        contentEmpty.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        adapter = new ProdutoRecyclerAdapter(produtos, getContext());
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        contentEmpty.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    adapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    contentEmpty.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            };
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mRecyclerView.setVisibility(View.GONE);
            Util.mostrarMensagen(getContext(), "Sem internet");
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
