package com.redmart.assignment.model.product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product {

    @SerializedName("product")
    @Expose
    private ProductInfo product;

    public ProductInfo getProduct() {
        return product;
    }

    public void setProducts(ProductInfo product) {
        this.product = product;
    }
}
