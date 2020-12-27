package com.augmented_reality.AR_Based_E_Commerce;


import java.util.Date;

public class Order {

        public String order_id;
        public String product_name;
        public String product_price;
        public String order_date;
        public Date purchase_date;

        public Order(String order_id, String product_name, String product_price, String order_date) {
            this.order_id = order_id;
            this.product_name = product_name;
            this.product_price = product_price;
            this.order_date = order_date;
        }

    public Order(String order_id, String product_name, String product_price, Date purchase_date) {
        this.order_id = order_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.purchase_date = purchase_date;
    }

    }