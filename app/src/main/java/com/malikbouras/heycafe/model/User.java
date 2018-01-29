package com.malikbouras.heycafe.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@IgnoreExtraProperties
public class User {
    private String name;
    private int coffees;

    public User() {
    }

    public User(String name, int coffees) {
        this.name = name;
        this.coffees = coffees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoffees() {
        return coffees;
    }

    public void setCoffees(int coffees) {
        this.coffees = coffees;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("coffees", coffees);
        result.put("user", name);

        return result;
    }
}
