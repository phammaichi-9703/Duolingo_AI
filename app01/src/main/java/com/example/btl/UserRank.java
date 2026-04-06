package com.example.btl;

public class UserRank {
    private int rank;
    private String name;
    private int xp;
    private String avatarUrl; // Optional for now, but good to have

    public UserRank(int rank, String name, int xp) {
        this.rank = rank;
        this.name = name;
        this.xp = xp;
    }

    public int getRank() { return rank; }
    public String getName() { return name; }
    public int getXp() { return xp; }
}
