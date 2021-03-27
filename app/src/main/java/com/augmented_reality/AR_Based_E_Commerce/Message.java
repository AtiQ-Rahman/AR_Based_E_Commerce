package com.augmented_reality.AR_Based_E_Commerce;

public class Message {

    public String message;
    public String sender_id;
    public String receiver_id;
    public String receiver_device_id;
    public String time;

    public Message(String sender_id,String receiver_id,String receiver_device_id,String message,String time) {

        this.message = message;
        this.sender_id = sender_id;
        this.receiver_id=receiver_id;
        this.receiver_device_id=receiver_device_id;
        this.time = time;
    }
}
