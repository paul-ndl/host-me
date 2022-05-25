package ch.epfl.sweng.hostme.utils;

import com.google.firebase.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.hostme.utils.Constants;

public class Apartment {
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
    private boolean available;
    private String proprietor;
    private String uid;
    private int utilities;
    private int deposit;
    private int duration;
    private Timestamp currentLease;
    private String docId;

    /**
     * Constructor for Firebase class binding
     */
    public Apartment() {
    }

    /**
     * Constructor of a residence listing. Stores all provided fields, sets default values for
     * fields that do not exist at instantiation, and checks for optional values.
     *
     * @param fields a JSONObject containing all required fields of the Add UI, optionally
     *               containing some additional fields
     * @throws JSONException when input data is malformed
     */
    public Apartment(JSONObject fields) {
        try {
            this.name = fields.getString(Constants.NAME);
            this.room = fields.getString(Constants.ROOM);
            this.address = fields.getString(Constants.ADDRESS);
            this.npa = fields.getInt(Constants.NPA);
            this.city = fields.getString(Constants.CITY);
            this.rent = fields.getInt(Constants.RENT);
            this.beds = fields.getInt(Constants.BEDS);
            this.area = fields.getInt(Constants.AREA);
            this.furnished = fields.getBoolean(Constants.FURNISHED);
            this.bath = fields.getString(Constants.BATH);
            this.kitchen = fields.getString(Constants.KITCHEN);
            this.laundry = fields.getString(Constants.LAUNDRY);
            this.pets = fields.getBoolean(Constants.PETS);
            this.imagePath = fields.getString(Constants.IMAGE_PATH);
            this.proprietor = fields.getString(Constants.PROPRIETOR);
            this.uid = fields.getString(Constants.UID);
            this.utilities = fields.getInt(Constants.UTILITIES);
            this.deposit = fields.getInt(Constants.DEPOSIT);
            this.duration = fields.getInt(Constants.DURATION);

            this.available = true;
            this.currentLease = null;
        } catch (Exception ignored) {
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

    public int getBeds() {
        return this.beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
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

    public String getDocId() {
        return this.docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    /**
     * A JSON representation of a Listing that may be used to create identical objects or edit the
     * fields of this Listing.
     *
     * @return JSONObject holding approximate representations of all this Listing's fields
     */
    public JSONObject exportDoc() {
        JSONObject ret = new JSONObject();
        try {
            ret.put(Constants.NAME, this.name);
            ret.put(Constants.ROOM, this.room);
            ret.put(Constants.ADDRESS, this.address);
            ret.put(Constants.NPA, this.npa);
            ret.put(Constants.CITY, this.city);
            ret.put(Constants.RENT, this.rent);
            ret.put(Constants.BEDS, this.beds);
            ret.put(Constants.AREA, this.area);
            ret.put(Constants.FURNISHED, this.furnished);
            ret.put(Constants.BATH, this.bath);
            ret.put(Constants.KITCHEN, this.kitchen);
            ret.put(Constants.LAUNDRY, this.laundry);
            ret.put(Constants.PETS, this.pets);
            ret.put(Constants.IMAGE_PATH, this.imagePath);
            ret.put("available", this.available);
            ret.put(Constants.PROPRIETOR, this.proprietor);
            ret.put(Constants.UID, this.uid);
            ret.put(Constants.UTILITIES, this.utilities);
            ret.put(Constants.DEPOSIT, this.deposit);
            ret.put(Constants.DURATION, this.duration);
            ret.put("currentLease", this.currentLease);
            ret.put("docId", this.docId);
        } catch (Exception ignored) {
        }

        return ret;
    }

    @Override
    public String toString() {
        return this.exportDoc().toString();
    }
}
