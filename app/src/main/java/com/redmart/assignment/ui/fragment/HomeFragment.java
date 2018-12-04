package com.redmart.assignment.ui.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.redmart.assignment.R;
import com.redmart.assignment.adapter.PaginationAdapter;
import com.redmart.assignment.listener.RecyclerTouchListener;
import com.redmart.assignment.model.AllTrendingProducts;
import com.redmart.assignment.model.Responses;
import com.redmart.assignment.retrofit.ProductApi;
import com.redmart.assignment.retrofit.ProductApiService;
import com.redmart.assignment.ui.activity.MainActivity;
import com.redmart.assignment.utility.PaginationAdapterCallback;
import com.redmart.assignment.utility.PaginationScrollListener;
import java.util.List;
import java.util.concurrent.TimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getName();
    private PaginationAdapter adapter;
    private GridLayoutManager layoutManager;

    private RecyclerView productView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button retryButton;
    private TextView errorText;

    private static final int PAGE_START = 20;
    private static final int PAGE = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 500; // due to huge data of RedMart ..I am giving a approx value of 500 page
    private int currentPage = PAGE_START;
    private ProductApiService productApiService;
    private MainActivity activity;

    private List<Responses> results;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        //initialize widgets
        initializeWidgets(view);
        return  view;
    }

    private void initializeWidgets(View view) {
        productView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        retryButton = (Button) view.findViewById(R.id.error_btn_retry);
        errorText = (TextView) view.findViewById(R.id.error_txt_cause);
        //initialize adapter
        adapter = new PaginationAdapter(activity,paginationAdapterCallback);
        layoutManager = new GridLayoutManager(activity, 3);
        productView.setLayoutManager(layoutManager);
        //productView.setItemAnimator(new DefaultItemAnimator());
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        productView.setLayoutAnimation(animation);
        productView.setAdapter(adapter);
        // row click listener
        productView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), productView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(results != null){
                    Responses responses = results.get(position);
                    ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(ProductDetailsFragment.PRODUCT_ID,responses.getId());
                    productDetailsFragment.setArguments(bundle);
                    MainActivity.fragmentManager.beginTransaction().replace(R.id.container,productDetailsFragment,"product_details").addToBackStack(null).commit();
                    Toast.makeText(getActivity(), "You Selected ! " + responses.getTitle(), Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG,"There is a problem, Contact RedMart!");
                    // send issue to developer team here
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                // not implemented ! its a assignment task
            }
        }));



        productView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 20;

                loadNextData();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        productApiService = ProductApi.getClient().create(ProductApiService.class);

        loadInitialData();

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInitialData();
            }
        });
    }
    private void loadNextData() {
        Log.d(TAG, "loadNextData: " + currentPage);
        callTopRatedProductsApi().enqueue(new Callback<AllTrendingProducts>() {
            @Override
            public void onResponse(Call<AllTrendingProducts> call, Response<AllTrendingProducts> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<AllTrendingProducts> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private void loadInitialData() {
        hideErrorView();

        callTopRatedProductsApi().enqueue(new Callback<AllTrendingProducts>() {
            @Override
            public void onResponse(Call<AllTrendingProducts> call, Response<AllTrendingProducts> response) {
                hideErrorView();
                results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<AllTrendingProducts> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }

    private List<Responses> fetchResults(Response<AllTrendingProducts> response) {
        AllTrendingProducts allTrendingProducts = response.body();
        return allTrendingProducts.getResults();
    }

    private Call<AllTrendingProducts> callTopRatedProductsApi() {
        return productApiService.getAllProducts(PAGE,currentPage
        );
    }

    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            errorText.setText(fetchErrorMessage(throwable));
        }
    }

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private PaginationAdapterCallback paginationAdapterCallback = new PaginationAdapterCallback() {
        @Override
        public void retryPageLoad() {
            loadNextData();
        }
    };
}
