package com.devansh.pixsel.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devansh.pixsel.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ListFragment extends Fragment {

    @BindView(R.id.floatingActionButton)                 // binding the fab button
    FloatingActionButton fab;
    public ListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this,view);                    //binding the view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // fab.setOnClickListener( view1 -> onGoToDetails());       i will implement this as add button later
    }
/*
    private void onGoToDetails() {

      // here i will  define the add image functionality of fab

    }


 */
}
