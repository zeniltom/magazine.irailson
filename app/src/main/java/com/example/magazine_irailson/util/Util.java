package com.example.magazine_irailson.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.magazine_irailson.R;
import com.example.magazine_irailson.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Util {


    public static boolean isCampoVazio(String valor) {

        return (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
    }

    public static boolean verifyConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static String mascaraData(Date data) {

        String formato = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(
                formato, new Locale("pt", "BR"));

        return sdf.format(data);
    }

    public static void mostrarMensagen(Context context, String mensagem) {

        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog mostrarProgressDialog(Context context, String mensagem) {

        ProgressDialog progressDialog = new ProgressDialog(context, R.style.dialogNordesteEmpregos);
        progressDialog.setMessage(mensagem);
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }

    public static void mostrarAlertDialog(Context context, String mensagem) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.dialogNordesteEmpregos);
        alertDialog.setTitle("Aviso");
        alertDialog.setMessage(mensagem);
        alertDialog.setNegativeButton("OK", null);
        alertDialog.show();
    }

    public static void mostrarInfoDialog(Context context, String mensagem) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.dialogNordesteEmpregos);
        alertDialog.setTitle("Informação");
        alertDialog.setMessage(mensagem);
        alertDialog.setNegativeButton("OK", null);
        alertDialog.show();
    }

    public static void enviarFotoPerfil(FirebaseAuth user, Uri local, String tipo) {

        StorageReference stream = ConfiguracaoFirebase.getFirebaseStorage()
                .child(tipo)
                .child("imagens")
                .child((user.getCurrentUser()).getUid())
                .child("perfil.JPEG");

        stream.putFile(local).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("#Upload Imagem", "Sucesso");
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("#Upload Imagem", "Falha: " + e.getMessage());
            }
        });
    }

    public static String dataPostagem(String vagaDataAnuncio, Context c) {
        Locale locale = c.getResources().getConfiguration().locale;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", locale);
        Date dataHoje = new Date();
        Date dataAnuncio = null;
        long diferenca = 0;

        try {
            dataAnuncio = format.parse(vagaDataAnuncio);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dataAnuncio != null) {
            diferenca = (dataHoje.getTime() - dataAnuncio.getTime());
        }

        long diferencaSegundos = diferenca / (1000);
        long diferencaMinutos = diferenca / (1000 * 60);
        long diferencaHoras = diferenca / (1000 * 60 * 60);
        long diferencaDias = diferenca / (1000 * 60 * 60 * 24);
        long diferencaMeses = diferenca / (1000 * 60 * 60 * 24) / 30;

        if (diferencaMeses > 0) return ("Há " + diferencaMeses + " meses");

        else if (diferencaDias > 0) return ("Há " + diferencaDias + " dias");

        else if (diferencaHoras > 0) return ("Há " + diferencaHoras + " horas");

        else if (diferencaMinutos > 0) return ("Há " + diferencaMinutos + " minutos");

        else if (diferencaSegundos > 0) return ("Há alguns segundos");

        return vagaDataAnuncio;
    }
}
