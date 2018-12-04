package com.redmart.assignment.ui.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.redmart.assignment.R;
import com.redmart.assignment.model.AllTrendingProducts;
import com.redmart.assignment.model.Responses;
import com.redmart.assignment.model.product.Product;
import com.redmart.assignment.model.product.ProductInfo;
import com.redmart.assignment.retrofit.ProductApi;
import com.redmart.assignment.retrofit.ProductApiService;
import com.redmart.assignment.ui.activity.MainActivity;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProductDetailsFragment.class.getName();
    public static final String PRODUCT_ID = "product_id";
    private int productId;
    private MainActivity activity;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button retryButton;
    private TextView errorText;
    private TextView productIdView;
    private TextView productTitleView;
    private TextView productDescriptionView;
    private Button addToCart;
    private ProductApiService productApiService;
    private ProductInfo result;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        //initialize widgets
        initializeWidgets(view);
        //init service and load data
        productApiService = ProductApi.getClient().create(ProductApiService.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            productId = bundle.getInt(PRODUCT_ID, 0);
        }
        loadProductDetailsData();
        return view;
    }

    private void initializeWidgets(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        retryButton = (Button) view.findViewById(R.id.error_btn_retry);
        errorText = (TextView) view.findViewById(R.id.error_txt_cause);
        productIdView = (TextView) view.findViewById(R.id.txt_product_id);
        productTitleView = (TextView) view.findViewById(R.id.txt_product_title);
        productDescriptionView = (TextView) view.findViewById(R.id.txt_product_desc);
        addToCart = (Button)view.findViewById(R.id.btn_add_to_kart);
        addToCart.setOnClickListener(this);
    }

    private void loadProductDetailsData() {

        hideErrorView();

        callProductDetailsApi().enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                hideErrorView();
                result = fetchResults(response);
                if(result != null){
                    populateProductDetailsData(result);
                }else{
                    Toast.makeText(getActivity(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }

    private void populateProductDetailsData(ProductInfo result) {
        if(productIdView != null){
            productIdView.setText("ID : "+result.getId());
        }
        if(productTitleView != null){
            productTitleView.setText("TITLE : "+result.getTitle());
        }

        if(productDescriptionView != null){
            productDescriptionView.setText("DESCRIPTION : "+result.getDesc());
        }

    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private Call<Product> callProductDetailsApi() {
        return productApiService.getProduct(productId
        );
    }

    private ProductInfo fetchResults(Response<Product> response) {
        Product product = response.body();
        return product.getProduct();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_to_kart:
                Toast.makeText(getActivity(),"Add To Cart Clicked!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
