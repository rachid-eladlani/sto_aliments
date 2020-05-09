
package com.ihm.stoaliment.consommateur.autour;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

//import com.example.map.R;
import com.ihm.stoaliment.R;
import com.ihm.stoaliment.consommateur.BaseConsommateurActivity;
import com.ihm.stoaliment.controleur.AuthentificationControleur;
import com.ihm.stoaliment.controleur.ProducteurControleur;
import com.ihm.stoaliment.model.Authentification;
import com.ihm.stoaliment.model.Consommateur;
import com.ihm.stoaliment.model.Producteur;

public class AutourActivity extends BaseConsommateurActivity implements Observer {
    private MapView map;
    private double lat;
    private double lng;
    IMapController mapController;
    ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;

    private ProducteurControleur producteurControleur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=new Intent(AutourActivity.this,GeolocalisationActivity.class);
        startActivityForResult(intent, 2);// Activity is started with requestCode 2

        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(   getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) );
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        //setContentView(R.layout.activity_map);

        producteurControleur = new ProducteurControleur(this);
        producteurControleur.addObserver(this);
        producteurControleur.loadProducteurs();

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);   //render

        /**
         * Zoomable
         */
        map.setBuiltInZoomControls(true);

        /**
         * permet de zommer avec 2 doigt
         */
        map.setMultiTouchControls(true);


        mapController = map.getController();
        mapController.setZoom(18.0);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2 && data!=null)
        {
            lat =data.getExtras().getDouble("latitude");
            lng =data.getExtras().getDouble("longitude");
            System.out.println(lat);
            System.out.println(lng);

            GeoPoint startPoint = new GeoPoint(lat, lng);
            com.google.firebase.firestore.GeoPoint curPos = new com.google.firebase.firestore.GeoPoint(lat,lng);
            mapController.setCenter(startPoint);

            Consommateur consommateur = Authentification.consommateur;
            consommateur.setLocationConsommateur(curPos);


            Log.d("info", "id consommateur :  " +  Authentification.consommateur.getId());

            //create a new item to draw on the map
            //your items
            List<OverlayItem> items = new ArrayList<OverlayItem>();
            OverlayItem home = new OverlayItem("Salade / Tomate / Oignon", "Siège social", new GeoPoint(43.132988,5.993595));
            Drawable m = home.getMarker(2);

            Resources res = getResources();


            //Drawable iconMap = res.getDrawable(R.drawable.ic_place_black_24dp);
            //Drawable iconResized = resize(res, iconMap, 20);

            OverlayItem curPosition = new OverlayItem("Vous etes ici ", "votre position", startPoint);

            items.add(curPosition);
            items.add(home); // Lat/Lon decimal degrees
            items.add(new OverlayItem("Jean-Luc l'agriculteur", "bah chez Jean-luc", new GeoPoint(43.131459,5.994371))); // Lat/Lon decimal degrees

            //the Place icons on the map with a click listener
            mMyLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, items, producteurControleur);


            mMyLocationOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mMyLocationOverlay);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    public int getBottomNavigationMenuItemId() {
        return R.id.action_autour;
    }


    private Drawable resize(Resources r, Drawable image, int newSize) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, newSize, newSize, false);
        BitmapDrawable drawableBmp = new BitmapDrawable(r, bitmapResized);
        return drawableBmp;
    }


    /*@Override
    public void update(Observable o, Object arg) {

        if(arg != null && mMyLocationOverlay != null){
            Producteur producteur = (Producteur) arg;

            mMyLocationOverlay.addItem(new OverlayItem(producteur.getId(), producteur.getNom(), producteur.getVille(), new GeoPoint(producteur.getLocation().getLatitude(), producteur.getLocation().getLongitude())));

            System.out.println(producteur.getNom());
            System.out.println(producteur.getLocation());
        }
    }*/
    @Override
    public void update(Observable o , Object arg){
        if(arg != null && mMyLocationOverlay != null){
            List<Producteur> producteurs = (List<Producteur>) arg;
            for(Producteur producteur : producteurs){
                mMyLocationOverlay.addItem(new OverlayItem(producteur.getId(),producteur.getNom(),producteur.getVille(),new GeoPoint(producteur.getLocation().getLatitude(),producteur.getLocation().getLongitude())));
                System.out.println(producteur.getNom());
                System.out.println(producteur.getLocation());
            }
        }
    }

    public void clearOverlay(){
        map.getOverlay().clear();
    }

    public void setFilteredProductors(List<Producteur> productors){
        for (Producteur p : productors) {
            mMyLocationOverlay.addItem(new OverlayItem(p.getId(), p.getNom(), p.getVille(), new GeoPoint(p.getLocation().getLatitude(), p.getLocation().getLongitude())));
        }

    }
}
