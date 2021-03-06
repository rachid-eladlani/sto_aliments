package com.ihm.stoaliment.producteur.abonneList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihm.stoaliment.R;
import com.ihm.stoaliment.model.Consommateur;

public class AbonneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abonne);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Consommateur consommateur = (Consommateur) bundle.getSerializable("ABONNE");

        if(consommateur!=null) {
            ((TextView) findViewById(R.id.nomAbonne)).setText(consommateur.getNom());
        }
    }
}
