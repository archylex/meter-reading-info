package io.archylex.meterreadingsinfo;

public class MeterService {
    public static final String table_name = "services";
    public static final String column_id = "ID";
    public static final String column_organization = "ORGANIZATION";
    public static final String column_login = "LOGIN";
    public static final String column_password = "PASSWORD";
    public static final String column_image_id = "IMAGE_ID";
    public static final String column_enabled = "ENABLED";

    private int id;
    private String organization;
    private String login;
    private String password;
    private int image_id;
    private int enabled;

    public static final String create_table = "CREATE TABLE " + table_name + "("
            + column_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + column_organization + " TEXT,"
            + column_login + " TEXT,"
            + column_password + " TEXT,"
            + column_image_id + " INTEGER,"
            + column_enabled + " INTEGER"
            + ")";

    public MeterService() {

    }

    public MeterService(int id, String organization, String login, String password, int image_id, int enabled) {
        this.id = id;
        this.organization = organization;
        this.login = login;
        this.password = password;
        this.image_id = image_id;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getImageId() {
        return image_id;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImageId(int id) {
        this.image_id = id;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

}