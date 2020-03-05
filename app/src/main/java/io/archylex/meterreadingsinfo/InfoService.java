package io.archylex.meterreadingsinfo;

public class InfoService {
    private String organization;
    private String subscriber_name;
    private String subscriber_address;
    private String subscriber_number;
    private String saldo;
    private String date;
    private String last_reading;
    private int image_id;
    private int id;
    private int service_id;
    private boolean online = false;

    public static final String table_name = "info";
    public static final String column_id = "ID";
    public static final String column_organization = "ORGANIZATION";
    public static final String column_subscriber_name = "NAME";
    public static final String column_address = "ADDRESS";
    public static final String column_subscriber_number = "SUBSCRIBER_NUMBER";
    public static final String column_saldo = "SALDO";
    public static final String column_date = "DATE";
    public static final String column_last_reading = "LAST_READING";
    public static final String column_image_id = "IMAGE_ID";
    public static final String column_service_id = "SERVICE_ID";
    public static final String column_online = "ONLINE";

    public static final String create_table = "CREATE TABLE " + table_name + "("
            + column_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + column_organization + " TEXT,"
            + column_subscriber_name + " TEXT,"
            + column_address + " TEXT,"
            + column_subscriber_number + " TEXT,"
            + column_saldo + " TEXT,"
            + column_date + " TEXT,"
            + column_last_reading + " TEXT,"
            + column_image_id + " INTEGER,"
            + column_online + " INTEGER,"
            + column_service_id + " INTEGER"
            + ")";

    public InfoService() {

    }

    public InfoService(int id, int image_id, String organization, String subscriber_name, String subscriber_address, String subscriber_number, String saldo, String date, String last_reading, int service_id) {
        this.id = id;
        this.service_id = service_id;
        this.image_id = image_id;
        this.organization = organization;
        this.subscriber_name = subscriber_name;
        this.subscriber_address = subscriber_address;
        this.subscriber_number = subscriber_number;
        this.saldo = saldo;
        this.date = date;
        this.last_reading = last_reading;
    }

    public int getId() {
        return id;
    }

    public int getServiceId() {
        return service_id;
    }

    public int getImageId() {
        return image_id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getSubscriberName() {
        return subscriber_name;
    }

    public String getSubscriberAddress() {
        return subscriber_address;
    }

    public String getSubscriberNumber() {
        return subscriber_number;
    }

    public String getSaldo() {
        return saldo;
    }

    public String getDate() {
        return date;
    }

    public String getLastReading() {
        return last_reading;
    }

    public boolean getOnline() {
        return online;
    }

    public void setImageId(int id) {
        this.image_id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setServiceId(int service_id) {
        this.service_id = service_id;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setSubscriberName(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    public void setSubscriberAddress(String subscriber_address) {
        this.subscriber_address = subscriber_address;
    }

    public void setSubscriberNumber(String subscriber_number) {
        this.subscriber_number = subscriber_number;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLastReading(String last_reading) {
        this.last_reading = last_reading;
    }
}