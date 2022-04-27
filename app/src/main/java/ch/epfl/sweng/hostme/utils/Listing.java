package ch.epfl.sweng.hostme.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.hostme.database.Storage;

import static ch.epfl.sweng.hostme.utils.Constants.APARTMENTS;

public class Listing {
    private String name;
    private String room;
    private String address;
    private int npa;
    private String city;
    private int rent;
    private int beds;
    private int area;
    private boolean furnished;
    private String bath;
    private String kitchen;
    private String laundry;
    private boolean pets;
    private String imagePath;
    private List<Bitmap> images;
    private boolean available;
    private String proprietor;
    private String uid;
    private int utilities;
    private int deposit;
    private int duration;
    private Timestamp currentLease;

    private Map<String, String> opt;

    /**
     * Constructor for Firebase class binding
     */
    public Listing () {
        this.parseImages();
    }

    /**
     * Constructor of a residence listing. Stores all provided fields, sets default values for
     * fields that do not exist at instantiation, and checks for optional values.
     *
     * @param fields a JSONObject containing all required fields of the Add UI, optionally
     *               containing some additional fields
     * @throws JSONException when input data is malformed
     */
    public Listing (JSONObject fields) {
        this.images = new ArrayList<>();
        this.opt = new HashMap<>();
        try {
            this.name = fields.getString("name");
            this.room = fields.getString("room");
            this.address = fields.getString("address");
            this.npa = fields.getInt("npa");
            this.city = fields.getString("city");
            this.rent = fields.getInt("rent");
            this.beds = fields.getInt("beds");
            this.area = fields.getInt("area");
            this.furnished = fields.getBoolean("furnished");
            this.bath = fields.getString("bath");
            this.kitchen = fields.getString("kitchen");
            this.laundry = fields.getString("laundry");
            this.pets = fields.getBoolean("pets");
            this.proprietor = fields.getString("proprietor");
            this.uid = fields.getString("uid");
            this.utilities = fields.getInt("utilities");
            this.deposit = fields.getInt("deposit");
            this.duration = fields.getInt("duration");

            this.imagePath = String.format("%s/%s_%s_%s", APARTMENTS, this.proprietor.toLowerCase(),
                    this.name.toLowerCase(), this.room.toLowerCase());
            this.available = true;
            this.currentLease = null;

            this.parseImages();
        } catch (org.json.JSONException e) { throw new RuntimeException(e); }
        JSONObject extra = fields.optJSONObject("opt");
        if (extra != null) {
            try {
                for (Iterator<String> key = extra.keys(); key.hasNext();) {
                    String it = key.next();
                    this.opt.put(it, extra.getString(it));
                }
            } catch (JSONException e) { throw new RuntimeException(e); }
        }

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNpa() {
        return this.npa;
    }

    public void setNpa(int npa) {
        this.npa = npa;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRent() {
        return this.rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public int getBeds() {
        return this.beds;
    }

    public int getArea() {
        return this.area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public boolean isFurnished() {
        return this.furnished;
    }

    public void toggleFurnished() {
        this.furnished = !this.furnished;
    }

    public String getBath() {
        return this.bath;
    }

    public void setBath(String bath) {
        this.bath = bath;
    }

    public String getKitchen() {
        return this.kitchen;
    }

    public void setKitchen(String kitchen) {
        this.kitchen = kitchen;
    }

    public String getLaundry() {
        return this.laundry;
    }

    public void setLaundry(String laundry) {
        this.laundry = laundry;
    }

    public boolean isPets() {
        return this.pets;
    }

    public void togglePets() {
        this.pets = !this.pets;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public void toggleAvailable() {
        this.available = !this.available;
    }

    public String getProprietor() {
        return this.proprietor;
    }

    public void setProprietor(String proprietor) {
        this.proprietor = proprietor;
    }

    public String getUid() {
        return this.uid;
    }

    public int getUtilities() {
        return this.utilities;
    }

    public void setUtilities(int utilities) {
        this.utilities = utilities;
    }

    public int getDeposit() {
        return this.deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getCurrentLease() {
        return this.currentLease;
    }

    public void setCurrentLease(Timestamp currentLease) {
        this.currentLease = currentLease;
    }

    public List<Bitmap> getImages() {
        return this.images;
    }

    public Map<String, String> getOpt() {
        return this.opt;
    }

    /**
     * A JSON representation of a Listing that may be used to create identical objects or edit the
     * fields of this Listing.
     *
     * @deprecated this was primarily used for local data validation when creating the workflow for
     *  the upload of listings to the database.
     *
     * @return JSONObject holding approximate representations of all this Listing's fields
     */
    @Deprecated
    public JSONObject exportDoc() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("name", this.name);
            ret.put("room", this.room);
            ret.put("address", this.address);
            ret.put("npa", this.npa);
            ret.put("city", this.city);
            ret.put("rent", this.rent);
            ret.put("beds", this.beds);
            ret.put("area", this.area);
            ret.put("furnished", this.furnished);
            ret.put("bath", this.bath);
            ret.put("kitchen", this.kitchen);
            ret.put("laundry", this.laundry);
            ret.put("pets", this.pets);
            ret.put("imagePath", this.imagePath);
            ret.put("available", this.available);
            ret.put("proprietor", this.proprietor);
            ret.put("uid", this.uid);
            ret.put("utilities", this.utilities);
            ret.put("deposit", this.deposit);
            ret.put("duration", this.duration);
            ret.put("currentLease", this.currentLease); // potential error

            for (String it: opt.keySet()) {
                ret.putOpt("opt." + it, opt.get(it));
            }
        } catch (JSONException e) { throw new RuntimeException(e); }

        return ret;
    }

    @Override
    public String toString() {
        return this.exportDoc().toString();
    }

    private void parseImages() {
        StorageReference imageDir = Storage.getStorageReferenceByChild(this.imagePath);
        imageDir.listAll().addOnSuccessListener(list -> {
            for (StorageReference item: list.getItems()) {
                item.getBytes(1000000).addOnSuccessListener(bytes -> {
                    images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                });
            }
        });
    }
}
