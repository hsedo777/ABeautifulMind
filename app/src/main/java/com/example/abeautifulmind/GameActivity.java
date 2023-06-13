package com.example.abeautifulmind;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abeautifulmind.model.GameActivityIO;
import com.example.abeautifulmind.model.MindAction;
import com.example.abeautifulmind.model.MindActionWrapper;
import com.example.abeautifulmind.model.MindTuple;
import com.example.abeautifulmind.model.Player;
import com.example.abeautifulmind.model.Strategy;
import com.example.abeautifulmind.model.tuple.NashEquilibriumException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final int MAX_VALUE = 6;

    private static final String AUTO_DESTROY_TRACE = "auto_destroy_trace";
    private GameActivityIO gameActivityIO;
    private MindTuple mindTuple;
    private List<Map<Integer, MindActionWrapper>> tuples;

    private Locale getCurrentLocale() {
        return getResources().getConfiguration().getLocales().get(0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTO_DESTROY_TRACE, true);
        //Save the state.
        gameActivityIO.toBundle(outState);
        mindTuple.toBundle(outState, tuples);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameActivityIO = new GameActivityIO(null, null, null, 0);
        TextView textView = findViewById(R.id.game_activity_result);
        if (savedInstanceState != null && savedInstanceState.containsKey(AUTO_DESTROY_TRACE)){
            //Catch auto new creation
            savedInstanceState.remove(AUTO_DESTROY_TRACE);
            gameActivityIO = gameActivityIO.fromBundle(savedInstanceState);
            tuples = new ArrayList<>();
            mindTuple = MindTuple.fromBundle(savedInstanceState, tuples);
            if (gameActivityIO.getPlayerChoice() != null){
                updateOnChoiceDone(true);
            } else {
                textView.setText(String.format(getString(R.string.game_activity_result), "_", "_", "_", "_"));
            }
        } else {
            gameActivityIO = gameActivityIO.fromIntent(getIntent());
            mindTuple = new MindTuple(2);
            textView.setText(String.format(getString(R.string.game_activity_result), "_", "_", "_", "_"));
            generateTuples();
            makeComputerChoice();
        }

        textView = findViewById(R.id.game_activity_opponent_strategy);
        textView.setText(String.format(getString(R.string.game_activity_opponent_strategy), gameActivityIO.getStrategy()));

        fillTable();

        Button button = findViewById(R.id.game_activity_action_a);
        button.setText(String.format(getString(R.string.game_activity_choose_action), MindAction.A));
        button.setOnClickListener(this::onActionTap);
        button.setEnabled(gameActivityIO.getStrategy() != Strategy.Nash);

        button = findViewById(R.id.game_activity_action_b);
        button.setText(String.format(getString(R.string.game_activity_choose_action), MindAction.B));
        button.setOnClickListener(this::onActionTap);
        button.setEnabled(gameActivityIO.getStrategy() != Strategy.Nash);

        button = findViewById(R.id.game_activity_dismiss_button);
        button.setOnClickListener(this::onDismissTap);
    }

    private void generateTuples() {
        tuples = mindTuple.generateTuples();
        Random random = new Random();
        int sign;
        for (Map<Integer, MindActionWrapper> element : tuples) {
            for (MindActionWrapper wrapper : element.values()) {
                sign = random.nextBoolean() ? 1 : -1;
                wrapper.setUtility(sign * random.nextInt(MAX_VALUE));
            }
        }
    }

    private void fillTable() {
        Player player = gameActivityIO.getPlayer(), computer = gameActivityIO.getComputer();
        TextView textView = findViewById(R.id.game_activity_table_cell_2_2);
        Map<Integer, MindActionWrapper> tuple = mindTuple.findByActions(Arrays.asList(MindAction.A, MindAction.A), tuples);
        textView.setText(tuple.get(player.getIndex()).getUtility() + "/" + tuple.get(computer.getIndex()).getUtility());

        textView = findViewById(R.id.game_activity_table_cell_2_3);
        tuple = mindTuple.findByActions(Arrays.asList(MindAction.A, MindAction.B), tuples);
        textView.setText(tuple.get(player.getIndex()).getUtility() + "/" + tuple.get(computer.getIndex()).getUtility());

        textView = findViewById(R.id.game_activity_table_cell_3_2);
        tuple = mindTuple.findByActions(Arrays.asList(MindAction.B, MindAction.A), tuples);
        textView.setText(tuple.get(player.getIndex()).getUtility() + "/" + tuple.get(computer.getIndex()).getUtility());

        textView = findViewById(R.id.game_activity_table_cell_3_3);
        tuple = mindTuple.findByActions(Arrays.asList(MindAction.B, MindAction.B), tuples);
        textView.setText(tuple.get(player.getIndex()).getUtility() + "/" + tuple.get(computer.getIndex()).getUtility());
    }

    private void onActionTap(View view) {
        gameActivityIO.setPlayerChoice(MindAction.valueOf(view.getTag().toString()));
        //The player chooses an action himself
        gameActivityIO.increaseGameCount();
        updateOnChoiceDone(false);
    }

    private void updateOnChoiceDone(boolean refresh) {
        Button button = findViewById(R.id.game_activity_action_a);
        button.setEnabled(false);
        button = findViewById(R.id.game_activity_action_b);
        button.setEnabled(false);

        Player player = gameActivityIO.getPlayer(), computer = gameActivityIO.getComputer();
        TextView textView = findViewById(R.id.game_activity_result);
        MindAction cc = gameActivityIO.getComputerChoice(), pc = gameActivityIO.getPlayerChoice();
        Map<Integer, MindActionWrapper> choice = mindTuple.findByActions(Arrays.asList(pc, cc), tuples);
        int playerReward = choice.get(player.getIndex()).getUtility();
        int computerReward = choice.get(computer.getIndex()).getUtility();
        String pr = String.format("%1$d", playerReward), cr = String.format("%1$d", computerReward);
        textView.setText(String.format(getString(R.string.game_activity_result), pc, cc, pr, cr));

        if(!refresh){
            computer.addToScore(computerReward);
            player.addToScore(playerReward);
        }

        int id = getResources().getIdentifier(String.format(getCurrentLocale(), "game_activity_table_cell_%1$d_%2$d", 2 + pc.ordinal(), 2 + cc.ordinal()), "id", getPackageName());
        textView = findViewById(id);
        textView.setBackgroundColor(getColor(R.color.pink));
    }

    private void onDismissTap(View v) {
        setResult(RESULT_OK, gameActivityIO.toIntent(null));
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, gameActivityIO.toIntent(null));
        super.onBackPressed();
    }

    private void makeComputerChoice() {
        MindAction action;
        switch (gameActivityIO.getStrategy()) {
            case Random:
                action = new Random().nextBoolean() ? (MindAction.A) : MindAction.B;
                break;
            case Greedy:
                int index = gameActivityIO.getComputer().getIndex();
                action = mindTuple.maxFor(index, tuples).get(index).getAction();
                break;
            case Cautious:
                action = mindTuple.minFor(gameActivityIO.getPlayer().getIndex(), tuples).get(gameActivityIO.getComputer().getIndex()).getAction();
                break;
            case Nash:
                findViewById(R.id.game_activity_action_a).setEnabled(false);
                findViewById(R.id.game_activity_action_b).setEnabled(false);
                String msg = "";
                try {
                    Map<Integer, MindActionWrapper> nash = nash();
                    MindActionWrapper playerNash = nash.get(gameActivityIO.getPlayer().getIndex());
                    MindActionWrapper computerNash = nash.get(gameActivityIO.getComputer().getIndex());
                    action = computerNash.getAction();
                    gameActivityIO.setComputerChoice(action);
                    gameActivityIO.setPlayerChoice(playerNash.getAction());
                    //The player chooses an action implicitly
                    gameActivityIO.increaseGameCount();
                    msg = getString(R.string.game_activity_unique_nash);
                    msg = String.format(msg, playerNash.getAction(), computerNash.getAction());
                    updateOnChoiceDone(false);
                } catch (NashEquilibriumException e) {
                    switch (e.getExceptionType()) {
                        case Multiple:
                            msg = getString(R.string.game_activity_multiple_nash);
                            break;
                        case None:
                            msg = getString(R.string.game_activity_no_nash);
                            break;
                    }
                    msg = String.format(msg);
                    action = null;
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                break;
            default:
                action = null;
        }
        gameActivityIO.setComputerChoice(action);
    }

    private Map<Integer, MindActionWrapper> nash() throws NashEquilibriumException {
        List<Map<Integer, MindActionWrapper>> nash = new ArrayList<>();
        Map<Integer, MindActionWrapper> aa = mindTuple.findByActions(Arrays.asList(MindAction.A, MindAction.A), tuples);
        Map<Integer, MindActionWrapper> ab = mindTuple.findByActions(Arrays.asList(MindAction.A, MindAction.B), tuples);
        Map<Integer, MindActionWrapper> ba = mindTuple.findByActions(Arrays.asList(MindAction.B, MindAction.A), tuples);
        Map<Integer, MindActionWrapper> bb = mindTuple.findByActions(Arrays.asList(MindAction.B, MindAction.B), tuples);
        int cid = gameActivityIO.getComputer().getIndex(), pid = gameActivityIO.getPlayer().getIndex();
        //aa
        if ((ba.get(pid).getUtility() <= aa.get(pid).getUtility()) && (ab.get(cid).getUtility() <= aa.get(cid).getUtility())) {
            nash.add(aa);
        }
        //ab
        if ((bb.get(pid).getUtility() <= ab.get(pid).getUtility()) && (aa.get(cid).getUtility() <= ab.get(cid).getUtility())) {
            nash.add(ab);
        }
        //ba
        if ((aa.get(pid).getUtility() <= ba.get(pid).getUtility()) && (bb.get(cid).getUtility() <= ba.get(cid).getUtility())) {
            nash.add(ba);
        }
        //bb
        if ((ab.get(pid).getUtility() <= bb.get(pid).getUtility()) && (ba.get(cid).getUtility() <= bb.get(cid).getUtility())) {
            nash.add(bb);
        }
        if (nash.isEmpty()) {
            throw new NashEquilibriumException(NashEquilibriumException.NashEquilibriumExceptionType.None);
        }
        if (nash.size() > 1) {
            throw new NashEquilibriumException(NashEquilibriumException.NashEquilibriumExceptionType.Multiple);
        }
        return nash.get(0);
    }
}