package com.surbhi.surbhivarday;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DataModel extends RealmObject {
    @PrimaryKey
    long id;
    String alias;
    String name;
    String privacy_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivacy_url() {
        return privacy_url;
    }

    public void setPrivacy_url(String privacy_url) {
        this.privacy_url = privacy_url;
    }
}
