package com.redmart.assignment.retrofit;

import com.redmart.assignment.model.AllTrendingProducts;
import com.redmart.assignment.model.product.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApiService {
    @GET("catalog/search")
    Call<AllTrendingProducts> getAllProducts(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("catalog/products/{product_id}")
    Call<Product> getProduct(
            @Path("product_id") int product_id
    );

}
