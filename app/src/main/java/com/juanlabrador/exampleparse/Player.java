package com.juanlabrador.exampleparse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.UUID;

/**
 * Created by juanlabrador on 19/10/15.
 */
@ParseClassName("Player")
public class Player extends ParseObject {

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }

    public void setScore(int score) {
        put("score", score);
    }

    public int getScore() {
        return getInt("score");
    }

    public void setUUID() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }

    public String getUUID() {
        return getString("uuid");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setCars(JSONArray cars) {
        put("cars", cars);
    }

    public JSONArray getCars() {
        return getJSONArray("cars");
    }


    public static ParseQuery<Player> getQuery() {
        return ParseQuery.getQuery(Player.class);
    }
}
