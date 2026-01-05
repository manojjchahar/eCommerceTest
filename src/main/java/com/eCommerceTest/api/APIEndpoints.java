package com.eCommerceTest.api;

import com.eCommerceTest.utils.ConfigReader;

public final class APIEndpoints {

    public static final String LOGIN = ConfigReader.getApiConfig("login_endpoint", "/api/verifyLogin");
    public static final String REGISTER = ConfigReader.getApiConfig("register_endpoint", "/api/createAccount");
    public static final String PRODUCTS_LIST = ConfigReader.getApiConfig("products_list_endpoint", "/api/productsList");
    public static final String SEARCH_PRODUCT = ConfigReader.getApiConfig("search_product_endpoint", "/api/searchProduct");
    public static final String BRANDS_LIST = ConfigReader.getApiConfig("brands_list_endpoint", "/api/brandsList");
    public static final String DELETE_ACCOUNT = ConfigReader.getApiConfig("delete_account_endpoint", "/api/deleteAccount");
    public static final String UPDATE_ACCOUNT = ConfigReader.getApiConfig("update_account_endpoint", "/api/updateAccount");
    public static final String GET_USER_BY_EMAIL = ConfigReader.getApiConfig("get_user_detail_by_email_endpoint", "/api/getUserDetailByEmail");

    private APIEndpoints() { }
}
