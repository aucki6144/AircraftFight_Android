package com.example.aircraftfight_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aircraftfight_android.R;
import com.example.aircraftfight_android.game.application.EasyGame;
import com.example.aircraftfight_android.game.application.Game;
import com.example.aircraftfight_android.game.application.GameCallback;
import com.example.aircraftfight_android.game.application.HardGame;
import com.example.aircraftfight_android.game.application.NormalGame;
import com.example.aircraftfight_android.game.application.OnlineGame;
import com.example.aircraftfight_android.helper.AuthenticationHelper;

public class GameActivity extends BaseActivity implements GameCallback
{
    private Game game;
    private TextView scoreView;
    private TextView lifeView;
    private TextView opponentScoreView;
    private ImageButton buttonPause;
    private ImageButton buttonResume;
    private FrameLayout grayMask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scoreView = findViewById(R.id.score_view);
        lifeView = findViewById(R.id.life_view);
        opponentScoreView = findViewById(R.id.opponent_score_view);

        buttonPause = findViewById(R.id.button_pause);
        buttonResume = findViewById(R.id.button_resume);
        grayMask = findViewById(R.id.gray_mask);

        grayMask.setVisibility(View.GONE);

        initGameView();
        initPauseAndResumeButton();
    }

    private void pauseGame()
    {
        game.pauseGame();
        buttonResume.setVisibility(View.VISIBLE);
        grayMask.setVisibility(View.VISIBLE);
    }

    private void resumeGame()
    {
        game.resumeGame();
        buttonResume.setVisibility(View.GONE);
        grayMask.setVisibility(View.GONE);
    }

    private void initPauseAndResumeButton()
    {
        buttonPause.setBackgroundColor(Color.TRANSPARENT);
        buttonResume.setBackgroundColor(Color.TRANSPARENT);

        buttonResume.setVisibility(View.GONE);

        buttonPause.setOnClickListener(v -> this.pauseGame());
        buttonResume.setOnClickListener(v -> this.resumeGame());
    }

    private void initGameView()
    {
        FrameLayout container = findViewById(R.id.game_container);

        String difficulty = getIntent().getStringExtra("difficulty");

        switch (difficulty) {
            case Game.EASY:
                game = new EasyGame(this, this);
                break;
            case Game.NORMAL:
                game = new NormalGame(this, this);
                break;
            case Game.HARD:
                game = new HardGame(this, this);
                break;
            default:
                AuthenticationHelper authHelper = new AuthenticationHelper(this);
                String playerId = authHelper.getUsername();
                game = new OnlineGame(this, this, playerId);
                break;
        }

        game.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(game);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(game.isPause()) {
                finish();
            }
            else {
                pauseGame();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        MainActivity.musicHelper.pauseBackgroundMusic();
        MainActivity.musicHelper.stopBossMusic();
        super.onDestroy();
    }

    @Override
    public void onGameOver(int score, String gameMode) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("gameMode", gameMode);
        startActivity(intent);

        this.finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onScoreChanged(int score) {
        scoreView.setText("Score: " + score);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLifeChanged(int hp) {
        lifeView.setText("Life: " + hp);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onOpponentScoreChanged(int score) {
        runOnUiThread(()->{
            opponentScoreView.setText("Opp:" + score);
        });
    }
}