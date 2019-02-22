package com.example.krampus.listapokemon.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.example.krampus.listapokemon.R;
import com.example.krampus.listapokemon.controler.PokeMainAdapter;
import com.example.krampus.listapokemon.interfaces.PokeInterfaceList;
import com.example.krampus.listapokemon.controler.PokeGetLista;
import com.example.krampus.listapokemon.model.Pokemon;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Retrofit retrofit;
    private static final String TAG = "POKEDEX";

    private RecyclerView recyclerView;
    private PokeMainAdapter listaPokemonAdapter;

    private int offset;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        listaPokemonAdapter = new PokeMainAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);

        toolbar.setTitle("PokeDex");
        setSupportActionBar(toolbar);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy >0){

                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if(flag) {
                        if ((visibleItemCount +pastVisibleItems ) >= totalItemCount) {

                            Log.i(TAG, " FIM");
                            flag = false;
                            offset += 20;
                            obterDatos(offset);
                        }
                    }
                }
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        flag = true;
        offset = 0;
        obterDatos(offset);
    }

    private void obterDatos(int offset) {
        PokeInterfaceList service = retrofit.create(PokeInterfaceList.class);
        Call<PokeGetLista> pokemonRespostaCall = service.obterListaPokemon(20,offset);

        pokemonRespostaCall.enqueue(new Callback<PokeGetLista>() {
            @Override
            public void onResponse(Call<PokeGetLista> call, Response<PokeGetLista> response) {
                flag = true;
                if(response.isSuccessful()){

                    PokeGetLista pokemonResposta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonResposta.getResults();
                    listaPokemonAdapter.adicionarListaPokemon(listaPokemon);

                } else
                    Log.e(TAG, " on response "+ response.errorBody());
            }

            @Override
            public void onFailure(Call<PokeGetLista> call, Throwable t) {
                flag = true;
                Log.e(TAG," on Failure "+ t.getMessage());
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){

            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
