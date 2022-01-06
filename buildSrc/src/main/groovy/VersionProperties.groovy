
class VersionProperties {
    private static final String versionCodeKey = "versionCode"
    private static final String versionNameKey = "versionName"

    private Properties properties

    static VersionProperties load(File file) {
        var properties = new Properties()
        properties.load(new FileInputStream(file))
        return new VersionProperties(properties)
    }

    private VersionProperties(Properties properties) {
        this.properties = properties
    }

    String getVersionName() {
        return properties.getProperty(versionNameKey)
    }

    void setVersionName(String name) {
        properties.setProperty(versionNameKey, name)
    }

    int getVersionCode() {
        return Integer.parseInt(properties.getProperty(versionCodeKey))
    }

    void setVersionCode(int code) {
        properties.setProperty(versionCodeKey, code.toString())
    }

    void store(File file) {
        properties.store(new FileOutputStream(file), "")
    }
}