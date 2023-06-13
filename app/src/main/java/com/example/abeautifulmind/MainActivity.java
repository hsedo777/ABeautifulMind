package com.example.abeautifulmind;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abeautifulmind.model.GameActivityIO;
import com.example.abeautifulmind.model.Player;
import com.example.abeautifulmind.model.PlayerResultContract;
import com.example.abeautifulmind.model.Strategy;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES_SOURCES = "SHARED_PREFERENCES_SOURCES";

    final private PlayerResultContract playerResultContract;
    private ActivityResultLauncher<GameActivityIO> launcher;
    final Player player, computer;
    private int gameCount;

    public MainActivity() {
        playerResultContract = new PlayerResultContract();
        player = new Player(0);
        computer = new Player(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_SOURCES, MODE_PRIVATE);
        player.mergeScore(sharedPreferences, GameActivityIO.PLAYER);
        computer.mergeScore(sharedPreferences, GameActivityIO.COMPUTER);
        gameCount = sharedPreferences.getInt(GameActivityIO.GAME_COUNT, 0);

        launcher = registerForActivityResult(playerResultContract, result -> {
            player.to(result.getPlayer());
            computer.to(result.getComputer());
            int tmp = result.getGameCount();
            if (tmp == gameCount) {
                //No choice done by player
                Toast.makeText(this, getString(R.string.game_activity_dismiss), Toast.LENGTH_SHORT).show();
            } else {
                gameCount = tmp;
                updateDashboard();
            }
        });

        Button button = findViewById(R.id.button_generate_game);
        button.setOnClickListener(this::generateGame);

        button = findViewById(R.id.button_reset_game);
        button.setOnClickListener(this::resetGame);

        updateDashboard();
    }

    public void generateGame(View view) {
        RadioGroup radioGroup = findViewById(R.id.strategies_radio_group);
        int cid = radioGroup.getCheckedRadioButtonId();
        if (cid == -1) {
            Toast.makeText(this, getResources().getText(R.string.msg_strategy_required), Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton radioButton = findViewById(cid);
        GameActivityIO io = new GameActivityIO(player, computer, Strategy.valueOf(radioButton.getText().toString()), gameCount);
        launcher.launch(io);
    }

    public void resetGame(View view) {
        player.reset();
        computer.reset();
        gameCount = 0;
        RadioGroup radioGroup = findViewById(R.id.strategies_radio_group);
        radioGroup.clearCheck();
        updateSharedPreferences();
        updateDashboard();
    }

    @SuppressLint("DefaultLocale")
    public void updateDashboard() {
        TextView textView = findViewById(R.id.play_count_view);
        textView.setText(String.format(getString(R.string.play_count_label), gameCount));

        textView = findViewById(R.id.statistics_user_score_value);
        textView.setText(String.format("%1$d", player.getScore()));

        textView = findViewById(R.id.statistics_computer_score_value);
        textView.setText(String.format("%1$d", computer.getScore()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateSharedPreferences();
    }

    private void updateSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_SOURCES, MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(GameActivityIO.COMPUTER, computer.getScore())
                .putInt(GameActivityIO.PLAYER, player.getScore())
                .putInt(GameActivityIO.GAME_COUNT, gameCount)
                .apply();
    }
}