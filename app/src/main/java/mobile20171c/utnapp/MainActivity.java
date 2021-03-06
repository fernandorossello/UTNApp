package mobile20171c.utnapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import Helpers.ImageLoader;
import Helpers.UrlRequest;
import mobile20171c.utnapp.Cursos.CursosFragment;
import mobile20171c.utnapp.Modelo.Usuario;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, android.support.v4.app.FragmentManager.OnBackStackChangedListener {

    private FirebaseAuth mAuth;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        Intent intent = getIntent();

        if (intent.getStringExtra("mensajesCurso") != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, CursosFragment.newInstanceMsg(intent.getStringExtra("mensajesCurso")), "Fragment")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, CursosFragment.newInstance(), "Fragment")
                    .commit();
        }


    }

    public void onBackStackChanged() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

            if (currentFragment instanceof getAppTitleListener) {
                // esto no esta funcionando muy bien
                setTitle(((getAppTitleListener) currentFragment).getAppTitle());
            } else {
                setTitle(fragmentTag);
            }
        } else {
            setTitle("UTN App");
        }
    }

    public interface getAppTitleListener {
        public String getAppTitle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        View headerLayout = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);

        TextView tvEmailUsuario = (TextView) headerLayout.findViewById(R.id.userMailTextView);
        tvEmailUsuario.setText(user.getEmail());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("usuarios").child(user.getUid()).child("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView tvNombreUsuario = (TextView) findViewById(R.id.userFullNameTextView);

                if (dataSnapshot.exists() && tvNombreUsuario != null) {

                    String nombre = dataSnapshot.getValue(String.class);

                    tvNombreUsuario.setText(nombre);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setImagenUsuario() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void desloguearUsuario() {
        mAuth.signOut();
        Intent intent =  new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mis_cursos) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, CursosFragment.newInstance(), "Fragment")
                    .addToBackStack("Cursos")
                    .commit();
        } else if (id == R.id.fechas) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, FechaFragment.newInstance(), "Fragment")
                    .addToBackStack("Mis fechas")
                    .commit();
        } else if (id == R.id.noticias) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, NoticiasFragment.newInstance(), "Fragment")
                    .addToBackStack("Noticias")
                    .commit();
        } else if (id == R.id.configuracion) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, ConfiguracionFragment.newInstance(), "Fragment")
                    .addToBackStack("Configuracion")
                    .commit();
        } else if (id == R.id.calendario){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, CalendarioFragment.newInstance(), "Fragment")
                    .addToBackStack("Calendario")
                    .commit();
        }
        else if (id == R.id.logout) {
            desloguearUsuario();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void downloadFromUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        ImageLoader.instance.execute(UrlRequest.makeRequest(url, new UrlRequest.Listener() {
            @Override
            public void onReceivedBody(int responseCode, byte body[]) {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(body, 0, body.length);
                ImageView ivFotoUsuario = (ImageView) findViewById(R.id.userFotoImageView);
                ivFotoUsuario.setImageBitmap(bitmap);
                }

            @Override
            public void onError(Exception e) {

            }
        }));
    }
}
