package me.matamor.ge.shared.database;

public enum DatabaseType {

    SQLITE,
    MYSQL;

    public static DatabaseType byName(String name) {
        for(DatabaseType type : values()) {
            if(type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

}
