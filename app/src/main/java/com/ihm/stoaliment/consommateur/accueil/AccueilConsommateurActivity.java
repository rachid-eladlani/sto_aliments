package com.ihm.stoaliment.consommateur.accueil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ihm.stoaliment.R;
import com.ihm.stoaliment.consommateur.autour.AutourActivity;
import com.ihm.stoaliment.controleur.ProducteurControleur;
import com.ihm.stoaliment.model.Producteur;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class AccueilConsommateurActivity extends AppCompatActivity implements Observer {

    private ProducteurListAdapter producteurListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_consommateur);

        Toolbar mytoolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mytoolbar);

        View BtnAutourDeMoi = findViewById(R.id.autourDeMoi);
        BtnAutourDeMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AutourActivity.class);
                startActivity(intent);
            }
        });

        ProducteurControleur producteurControleur = new ProducteurControleur(this);
        producteurControleur.addObserver(this);
        producteurControleur.loadProducteurs();

        List<Producteur> producteurs = new ArrayList<>();
        producteurListAdapter = new ProducteurListAdapter(this, producteurs);
        ListView listView = findViewById(R.id.listViewProducteur);
        listView.setAdapter(producteurListAdapter);
        listView.setOnItemClickListener(producteurControleur);


    }

    @Override
    public void update(Observable o, Object arg) {

        producteurListAdapter.add(arg);
        producteurListAdapter.notifyDataSetChanged();

        View load = findViewById(R.id.load);
        load.setVisibility(View.GONE);
    }
}
