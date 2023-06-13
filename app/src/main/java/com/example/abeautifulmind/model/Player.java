package com.example.abeautifulmind.model;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.abeautifulmind.model.tuple.PlayerInterface;

import java.io.Serializable;

public class Player implements Serializable, PlayerInterface {

    public static final String SCORE_KEY = "playerScore";
    public static final String INDEX_KEY = "playerIndex";

    private int index;
    private int score;

    public Player(int index) {
        this(index, 0);
    }

    public Player(int index, int score) {
        this.index = index;
        this.score = score;
    }

    public boolean isComputer() {
        return index == 1;
    }

    public int getIndex() {
        return index;
    }

    public int getScore() {
        return score;
    }

    public void reset() {
        score = 0;
    }

    public Intent toIntent(@Nullable Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra(Player.SCORE_KEY, getScore());
        intent.putExtra(Player.INDEX_KEY, getIndex());
        return intent;
    }

    public Player fromIntent(@NonNull Intent intent) {
        score = intent.getIntExtra(SCORE_KEY, 0);
        index = intent.getIntExtra(INDEX_KEY, 0);
        return this;
    }

    public Player to(Player input) {
        if (input != null) {
            score = input.score;
            index = input.index;
        }
        return this;
    }

    public void mergeScore(SharedPreferences sharedPreferences, String key) {
        if (key != null && sharedPreferences != null && sharedPreferences.contains(key)) {
            score = sharedPreferences.getInt(key, 0);
        }
    }

    public void addToScore(int toAdd) {
        score += toAdd;
    }
}