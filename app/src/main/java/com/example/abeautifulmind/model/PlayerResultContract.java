package com.example.abeautifulmind.model;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.abeautifulmind.GameActivity;

public class PlayerResultContract extends ActivityResultContract<GameActivityIO, GameActivityIO> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, GameActivityIO gameActivityIO) {
        Intent intent = new Intent(context, GameActivity.class);
        return gameActivityIO == null ? intent : gameActivityIO.toIntent(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public GameActivityIO parseResult(int i, @Nullable Intent intent) {
        return new GameActivityIO(null, null, null, 0).fromIntent(intent);
    }
}