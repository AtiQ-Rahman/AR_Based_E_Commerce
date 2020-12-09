package com.augmented_reality.AR_Based_E_Commerce;

public class Product implements Comparable<Product> {

    public String product_id;
    public String product_alt_id;
    public String user_id;
    public int price;
    public int payment_complete;
    public int charge;
    public String product_type;
    public String name;
    public String color;
    public String image_path;
    public String video_path;
    public String compress_image_path;

    public String sold_status;
    public int sold_price;

    public String admin_name;
    public String admin_location;
    public String admin_device_id;

    public Product(String product_id,String product_type,String product_alt_id,String user_id, String name, int price, String color,String image_path, String video_path) {

        this.product_id = product_id;
        this.product_type=product_type;
        this.product_alt_id=product_alt_id;
        this.user_id=user_id;
        this.name = name;
        this.price = price;
        this.color = color;
        this.image_path = image_path;
        this.video_path = video_path;

    }

    public Product(String product_id,String product_type,String product_alt_id,String user_id,String sold_status,int sold_price,String name, int price,  String color, String image_path, String video_path) {
        this.product_id = product_id;
        this.product_type=product_type;
        this.product_alt_id=product_alt_id;
        this.user_id=user_id;
        this.sold_status = sold_status;
        this.sold_price=sold_price;
        this.name = name;
        this.price = price;

        this.color = color;

        this.image_path = image_path;
        this.video_path = video_path;

    }
    public Product(String product_id,String product_alt_id, String user_id, String product_type, String name, int price,  String color, String image_path,String compress_image_path, String video_path) {

        this.product_id = product_id;
        this.product_alt_id=product_alt_id;
        this.user_id = user_id;
        this.product_type = product_type;
        this.name = name;
        this.price = price;
        this.color = color;
        this.image_path = image_path;
        this.compress_image_path=compress_image_path;
        this.video_path = video_path;

    }

    public Product(String product_id,String product_alt_id,String user_id,int payment_complete,int charge,String time, String product_type, String name, int price, String color, String image_path, String video_path,String compress_image_path) {

        this.product_id = product_id;
        this.product_alt_id=product_alt_id;
        this.user_id = user_id;
        this.payment_complete=payment_complete;
        this.charge=charge;
        this.color = color;
        this.product_type = product_type;
        this.name = name;
        this.price = price;
        this.image_path = image_path;
        this.video_path = video_path;

        this.compress_image_path = compress_image_path;
    }



    public String getId() {
        return product_id;
    }

    public void setId(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }


  
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /*public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }*/

    public String getSold_status() {
        return sold_status;
    }

    public void setSold_status(String sold_status) {
        this.sold_status = sold_status;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public String getAdmin_location() {
        return admin_location;
    }

    public String getAdmin_device_id() {
        return admin_device_id;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public void setAdmin_location(String admin_location) {
        this.admin_location = admin_location;
    }

    public void setAdmin_device_id(String admin_device_id) {
        this.admin_device_id = admin_device_id;
    }

    @Override
    public int compareTo(Product o) {
        int id1=Integer.parseInt(product_alt_id);
        int id2=Integer.parseInt(o.product_alt_id);
        if(id1>id2){

            return 1;
        }
        else return -1;
    }


}
