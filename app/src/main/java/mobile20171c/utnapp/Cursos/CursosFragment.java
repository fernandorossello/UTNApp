package mobile20171c.utnapp.Cursos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import mobile20171c.utnapp.R;
import mobile20171c.utnapp.Recycler.CursosRecyclerAdapter;


public class CursosFragment extends Fragment {

    public CursosFragment() {
        // Required empty public constructor
    }

    public static CursosFragment newInstance() {
        return new CursosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cursos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, NuevoCursoFragment.newInstance(), "Fragment")
                        .addToBackStack("Nuevo Curso")
                        .commit();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewCursos);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setAdapter(new CursosRecyclerAdapter(
                                        getContext(),
                                        FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid()).child("cursos")
                                    )
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}