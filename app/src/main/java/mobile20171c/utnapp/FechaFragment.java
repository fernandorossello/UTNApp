package mobile20171c.utnapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Dominio.modelo.Fecha;
import Dominio.repositorios.RepositorioCursos;
import Dominio.repositorios.RepositorioFechas;
import mobile20171c.utnapp.dummy.DummyContent;
import mobile20171c.utnapp.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

public class FechaFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    public FechaFragment() {
    }

    public static FechaFragment newInstance() {
        return new FechaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fecha_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

            //TODO: Reemplazar por el mail verdadero del usuario
            ArrayList<Fecha> fechasImportantes = new RepositorioFechas().getAllPara("emailUsuario");
            recyclerView.setAdapter(new MyFechaRecyclerViewAdapter(fechasImportantes, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Fecha item);
    }
}