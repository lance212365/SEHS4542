package com.example.sehs4542;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{

    // Imply the View.OnClickListener,View.OnTouchListener for the screen click function and the button function for starting the game.
    // Pre-declare the View on the top level of the class
    private List<Button> difficultButtons;
    private ConstraintLayout startingScreen;
    private LinearLayout selectDifficulty;
    private LinearLayout aboutPromptBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //===============Linking View Here===============
        startingScreen = findViewById(R.id.startingScreen);
        selectDifficulty = findViewById(R.id.selectDifficulty);
        aboutPromptBox = findViewById(R.id.aboutLayout);
        difficultButtons = Arrays.asList(
               (Button)findViewById(R.id.buttonEasy),
               (Button)findViewById(R.id.buttonNormal),
               (Button)findViewById(R.id.buttonAdvanced),
               (Button)findViewById(R.id.buttonArranged),
                (Button)findViewById(R.id.button5),
                (Button)findViewById(R.id.finishAbout)
       );
        //===============End of Linking View===============

        // After that, set the difficulties select of invisible unless the start screen is clicked.
        // Set the onTouchListener for the start Screen to start from anywhere of the screen.
        selectDifficulty.setVisibility(View.INVISIBLE);
        startingScreen.setOnTouchListener(this);
    }

    //
    private void setMenuButtonListener(){

        // Iterate over the buttons' view to set the onClick method binding.
        for(Button b:difficultButtons){
            b.setOnClickListener(this);
        }
    }
    private void startGame(int difficulty){

        // When starting the game, we will need the difficulties select to be hided
        // Then send the difficulty number with Extra of Integer Store as Intent,
        // sending the Intent to the deckboard activity.

        Animation fo1 = new AlphaAnimation(1.0f,0.0f);
        fo1.setDuration(600);
        selectDifficulty.startAnimation(fo1);
        Intent it = new Intent(this,Deckboard.class);
        it = it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("difficulty",difficulty);
        startActivity(it);
    }
    @Override
    public void onClick(View v) {
        // This binds all buttons with the difficulty numbers as argument of the function startGame()
        // With different number, the game will start with different difficulty.
        switch(v.getId()){
            case R.id.buttonEasy:
                startGame(0);
                break;
            case R.id.buttonNormal:
                startGame(1);
                break;
            case R.id.buttonAdvanced:
                startGame(2);
                break;
            case R.id.buttonArranged:
                startGame(3);
                break;
            case R.id.button5:
                aboutPromptBox.setVisibility(View.VISIBLE);
                break;
            case R.id.finishAbout:
                aboutPromptBox.setVisibility(View.INVISIBLE);
                break;
        }
    }
    // This implied method will kill the task.
    @Override
    public void onBackPressed()
    {
        finish();
    }
    // This is onTouch method for the StartScreen.
    // After touching the screen ,the difficulty select with fade-in , while the Touch Anywhere will fade-out.
    // Last but not least, start the buttons binding function at the end.
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                Animation fo1 = new AlphaAnimation(1.0f,0.0f);
                fo1.setDuration(600);
                Animation fi1 = new AlphaAnimation(0.0f,1.0f);
                fi1.setDuration(600);
                startingScreen.startAnimation(fo1);
                selectDifficulty.startAnimation(fi1);
                startingScreen.setVisibility(View.INVISIBLE);
                selectDifficulty.setVisibility(View.VISIBLE);
                setMenuButtonListener();
                break;
        }
        return false;
    }
}