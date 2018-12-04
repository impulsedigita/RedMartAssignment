package com.redmart.assignment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.redmart.assignment.R;
import com.redmart.assignment.constants.Constant;
import com.redmart.assignment.model.Responses;
import com.redmart.assignment.utility.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PaginationAdapter.class.getName();

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int PRODUCT = 2;

    private List<Responses> productResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public PaginationAdapter(Context context, PaginationAdapterCallback paginationAdapterCallback) {
        this.context = context;
        this.mCallback = paginationAdapterCallback;
        productResults = new ArrayList<>();
    }

    public List<Responses> getProducts() {
        return productResults;
    }

    public void setProducts(List<Responses> productResults) {
        this.productResults = productResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.product_item, parent, false);
                viewHolder = new ProductItem(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
            case PRODUCT:
                View viewProduct = inflater.inflate(R.layout.item_product, parent, false);
                viewHolder = new ProductViewHolder(viewProduct);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Responses response = productResults.get(position); // Products

        switch (getItemViewType(position)) {

            case PRODUCT:
                final ProductViewHolder productViewHolder = (ProductViewHolder) holder;

                productViewHolder.mProductName.setText(response.getTitle());
                productViewHolder.mPrice.setText("Price : $ " + response.getPricing().getPrice());

                loadImage(response.getImg().getName()).into(productViewHolder.mPosterImg);
                break;

            case ITEM:
                final ProductItem productItem = (ProductItem) holder;
                productItem.mProductName.setText(response.getTitle());
                productItem.mPrice.setText("Price : $ " + response.getPricing().getPrice());
                // load product image
                loadImage(response.getImg().getName())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                // Handle Failure here !! RedMart assignments
                                productItem.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                // image ready, hide progress now
                                productItem.mProgress.setVisibility(View.GONE);
                                return false;   // return false if you want Glide to handle everything else.
                            }
                        })
                        .into(productItem.mPosterImg);
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return productResults == null ? 0 : productResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM;
        } else {
            return (position == productResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    /* Helpers - bind Views */

    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(context)
                .load(Constant.BASE_URL_IMG + posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }


    /*   Helpers - Pagination   */
    public void add(Responses response) {
        productResults.add(response);
        notifyItemInserted(productResults.size() - 1);
    }

    public void addAll(List<Responses> productResults) {
        for (Responses response : productResults) {
            add(response);
        }
    }

    public void remove(Responses response) {
        int position = productResults.indexOf(response);
        if (position > -1) {
            productResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Responses());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = productResults.size() - 1;
        Responses result = getItem(position);

        if (result != null) {
            productResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Responses getItem(int position) {
        return productResults.get(position);
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(productResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    /**
     * Header ViewHolder
     */
    protected class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView mProductName;
        private TextView mPrice;
        private ImageView mPosterImg;

        public ProductViewHolder(View itemView) {
            super(itemView);

            mProductName = (TextView) itemView.findViewById(R.id.product_title);
            mPrice = (TextView) itemView.findViewById(R.id.product_price);
            mPosterImg = (ImageView) itemView.findViewById(R.id.product_image);
        }
    }

    /**
     * Main list's content ViewHolder
     */
    protected class ProductItem extends RecyclerView.ViewHolder {
        private TextView mProductName;
        private TextView mPrice;
        private ImageView mPosterImg;
        private ProgressBar mProgress;

        public ProductItem(View itemView) {
            super(itemView);

            mProductName = (TextView) itemView.findViewById(R.id.product_title);
            mPrice = (TextView) itemView.findViewById(R.id.product_price);
            mPosterImg = (ImageView) itemView.findViewById(R.id.product_image);
            mProgress = (ProgressBar) itemView.findViewById(R.id.product_progress);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.load_more_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.load_more_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.load_more_error);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.load_more_error_layout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.load_more_retry:
                case R.id.load_more_error_layout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

}
