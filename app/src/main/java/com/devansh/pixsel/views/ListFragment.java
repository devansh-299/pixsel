package com.devansh.pixsel.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devansh.pixsel.R;
import com.devansh.pixsel.viewmodel.ListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    @BindView(R.id.floatingActionButton)
//    binding the fab button
    FloatingActionButton fab;

    private ListViewModel viewModel;
    private ImageListAdapter imageListAdapter = new ImageListAdapter(new ArrayList<>());

    @BindView(R.id.recyclerView_list)
    RecyclerView imageList;

    @BindView(R.id.listError)
    TextView listError;

    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    public ListFragment() { }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        setBackgroundColor(view);
        ((MainActivity)getActivity()).setMyActionBar();
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        viewModel.refresh();

        /**
         * Below commented code is the layout for old design
         *
         *  imageList.setLayoutManager(new LinearLayoutManager(getContext()));
         *  imageList.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
         *
         */
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager
                .GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        imageList.setLayoutManager(layoutManager);
        imageList.setAdapter(imageListAdapter);
        observeViewModel();
        // TODO: implement fab's functionality
        fab.setOnClickListener( view1 -> onGoToDetails());
    }


    public void setBackgroundColor(View view){
        view.setBackgroundColor(getResources().getColor(R.color.completeBlack));
    }

    private void observeViewModel() {
        viewModel.images.observe(this, imageParameter -> {
//            its just a dummy variable # any name
            if(imageParameter!=null && imageParameter instanceof List){
                imageList.setVisibility(View.VISIBLE);
                imageListAdapter.updateImageList(imageParameter);
            }
        });

        viewModel.imageLoadError.observe(this, isError -> {
            if (isError != null && isError instanceof Boolean){
                listError.setVisibility(isError ? View.VISIBLE :View.GONE);
            }

        });

        viewModel.loading.observe(this, isLoading -> {
            if(isLoading!= null  && isLoading instanceof Boolean){
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading){
                    listError.setVisibility(View.GONE);
                    imageList.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onGoToDetails() {
        // TODO: Complete Add Image Functionality
        ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
        Navigation.findNavController(fab).navigate(action);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSettings: {
                if (isAdded()) {
                    Navigation.findNavController(getView()).navigate(
                            ListFragmentDirections.actionSettings());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
