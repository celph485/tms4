package tms.common;

public enum ConfigKey {
    TC_SERVER_URL("tc.server.url"),
    TC_SESSION_API_ENDPOINT("tc.session.api.endpoint"),
    TC_WS_ENDPOINT("tc.ws.endpoint"),
    TC_USERNAME("tc.username"),
    TC_PASSWORD("tc.password"),
    FT_ENABLE("ft.enable"),
    TC_WS_READ_DELAY_SECONDS("tc.ws.read.delay.seconds"),
    FT_API_ENDPOINT("ft.api.endpoint"),
    FT_API_VENDOR("ft.api.vendor"),
    FT_ALLOWED_DEVICES("ft.allowed.devices"),
    NICER_GLOBE_ENABLE("nicer.globe.enable"),
    NICER_GLOBE_API_ENDPOINT("nicer.globe.api.endpoint"),
    NICER_GLOBE_API_SECRET_KEY("nicer.globe.api.secret.key"),
    NICER_GLOBE_ALLOWED_DEVICES("nicer.globe.allowed.devices")
    ;

    private final String value;

    ConfigKey(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
