package com.redmart.assignment.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class AllTrendingProducts {
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("products")
    @Expose
    private List<Responses> products = new ArrayList<Responses>();
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("on_sale_count")
    @Expose
    private Integer on_sale_count;

    /**
     *
     * @return
     * The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     *
     * @return
     * The results
     */
    public List<Responses> getResults() {
        return products;
    }

    /**
     *
     * @param products
     * The results
     */
    public void setResults(List<Responses> products) {
        this.products = products;
    }

    /**
     *
     * @return
     * The totalResults
     */
    public Integer getTotalResults() {
        return total;
    }

    /**
     *
     * @param total
     * The total_results
     */
    public void setTotalResults(Integer total) {
        this.total = total;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public Integer getTotalPages() {
        return on_sale_count;
    }

    /**
     *
     * @param on_sale_count
     * The total_pages
     */
    public void setTotalPages(Integer on_sale_count) {
        this.on_sale_count = on_sale_count;
    }
}
