package com.example.abeautifulmind.model;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Wrapper of the instances used for input and output argument for activity GameActivity.
 *
 * @author hovozounkou
 */
public class GameActivityIO {

    public final static String STRATEGY = "strategy";
    public final static String PLAYER = "player";
    public final static String COMPUTER = "computer";
    public final static String GAME_COUNT = "count";;
    public final static String COMPUTER_CHOICE = "computerChoice";
    public final static String PLAYER_CHOICE = "playerChoice";

    private Player player, computer;
    private Strategy strategy;
    private MindAction playerChoice, computerChoice;
    private int gameCount;

    public GameActivityIO(Player player, Player computer, Strategy strategy, int gameCount) {
        this.player = player;
        this.computer = computer;
        this.strategy = strategy;
        this.gameCount = gameCount;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public MindAction getPlayerChoice() {
        return playerChoice;
    }

    public void setPlayerChoice(MindAction playerChoice) {
        this.playerChoice = playerChoice;
    }

    public MindAction getComputerChoice() {
        return computerChoice;
    }

    public void setComputerChoice(MindAction computerChoice) {
        this.computerChoice = computerChoice;
    }

    private void writeBundle(Bundle bundle, Player gamer) {
        String key = (gamer.isComputer() ? COMPUTER : PLAYER) + ".";
        bundle.putInt(key + Player.SCORE_KEY, gamer.getScore());
        bundle.putInt(key + Player.INDEX_KEY, gamer.getIndex());
    }

    private Player readBundle(Bundle bundle, String key) {
        String indKey = key + "." + Player.INDEX_KEY;
        String sccKey = key + "." + Player.SCORE_KEY;
        if (bundle.containsKey(indKey) || bundle.containsKey(sccKey)) {
            return new Player(bundle.getInt(indKey, 0), bundle.getInt(sccKey, 0));
        }
        return null;
    }

    public Bundle toBundle(@NonNull Bundle bundle) {
        writeBundle(bundle, player);
        writeBundle(bundle, computer);
        if (strategy != null) {
            bundle.putString(STRATEGY, Objects.toString(strategy));
        }
        if (playerChoice != null){
            bundle.putString(PLAYER_CHOICE, playerChoice.toString());
        }
        if (computerChoice != null){
            bundle.putString(COMPUTER_CHOICE, computerChoice.toString());
        }
        bundle.putInt(GAME_COUNT, gameCount);
        return bundle;
    }

    public Intent toIntent(@Nullable Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtras(toBundle(new Bundle()));
        return intent;
    }

    public GameActivityIO fromBundle(@Nullable Bundle bundle) {
        if (bundle != null) {
            player = readBundle(bundle, PLAYER);
            computer = readBundle(bundle, COMPUTER);
            strategy = Strategy.valueOf(bundle.getString(STRATEGY));
            gameCount = bundle.getInt(GAME_COUNT, 0);
            if (bundle.containsKey(PLAYER_CHOICE)){
                playerChoice = MindAction.valueOf(bundle.getString(PLAYER_CHOICE));
            }
            if (bundle.containsKey(COMPUTER_CHOICE)){
                computerChoice = MindAction.valueOf(bundle.getString(COMPUTER_CHOICE));
            }
        }
        return this;
    }

    public GameActivityIO fromIntent(@Nullable Intent intent) {
        return fromBundle(intent == null ? null : intent.getExtras());
    }

    public Player getPlayer() {
        return player;
    }

    public Player getComputer() {
        return computer;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void increaseGameCount() {
        gameCount++;
    }
}
