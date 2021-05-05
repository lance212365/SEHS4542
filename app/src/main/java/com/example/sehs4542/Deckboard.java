package com.example.sehs4542;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Deckboard extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {

    // View Components Declaration
    private TextView timer,difficultyText,timerResult,difficultyResult; // This will shows on both result and the header.
    private RelativeLayout deckboard; // The card deck layout.
    private LinearLayout congratsLayout; // The result layout.
    private ImageView[] cardImages; // This will store ImageViews of all cards avaliable
    private Button finishGame; // Button for finish game for good


    // Attribute for the difficulty
    int playCards =0,column=0, row=0,cardX=0,cardY=0,playNumber=0;
    // String Array for cards and difficulties
    private String[] difficulties = new String[]{"Easy","Normal","Advanced","Arranged"}; // Difficulties which will be selected on the Intent
    private String[] colors = new String[]{"c","d","h","s"}; // "c" for Club, "d" for "Diamond" , "h" for Heart, "s" for Spade
    private String[] numbers = new String[]{"a","2","3","4","5","6","7","8","9","10","j","q","k"}; // 13 numbers of the Poker cards
    // Given the card above, the colors*numbers should = 52, but the played card will be determined on the difficulty.

    // Integer Container
    private int cardWidth ,cardHeight;
    private int m,s; // Time in minutes and second.
    private int cx,cy; // Touched X and Y position

    // ArrayList for the cards actions and Hashmap for validating game
    private ArrayList<int[]> curcardspos; // Stores arrays of two integer, indicating {X,Y} position of the card anchor.
    private ArrayList<String> playableCards; // Stores strings of the playable cards
    private HashMap<String, Integer> cards; // Store the card name and it's drawable for render use
    private HashMap<Integer, String> solvedCards; // This will indicate if the card is "Solved" or "Unsolved"

    // This will store two matching cards for matching
    private String revealCard1,revealCard2;
    //Timer Container
    private Timer timerTicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //===============Linking View Here===============
        setContentView(R.layout.activity_deckboard);
        deckboard = (RelativeLayout) findViewById(R.id.deckboard);
        congratsLayout = (LinearLayout) findViewById(R.id.aboutLayout);
        timer = findViewById(R.id.timer);
        difficultyText =  findViewById(R.id.difficulty);
        timerResult = findViewById(R.id.timer2);
        difficultyResult =  findViewById(R.id.difficulty2);
        finishGame = findViewById(R.id.finishAbout);
        //===============End of Linking View===============


        congratsLayout.setVisibility(View.INVISIBLE); // Set the result into invisible component
        int difficulty = (int)getIntent().getIntExtra("difficulty",-1); // Get the integer from the Intent
        timer.setText("00:00"); // Set the text of the timer to 00:00 first , then begin timer.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buildCard(difficulty); // Build the playableCards with difficulty number
            }
        });

        // Animate the layout by fade in the layout, making the game start in few seconds.
        deckboard.setVisibility(View.INVISIBLE);
        Animation fi1 = new AlphaAnimation(0.0f,1.0f);
        fi1.setDuration(1000);
        deckboard.startAnimation(fi1);
        deckboard.setVisibility(View.VISIBLE);


        // We will have to make deck like picking full deck of poker,
        // To make sure every cards has its own drawable number.
        // Having numbers store doesn't mean that the drawable is rendered.
        makeDeck();
        tickTimer();
        deckboard.setOnTouchListener(this);
        finishGame.setOnClickListener(this);
    }
    private void makeDeck(){
        cards = new HashMap<String, Integer>();
        for(int c = 1;c<=4;c++){
            String color = colors[c-1]; // Respectively create from the card color
            for(int n =0;n<numbers.length;n++){
                String number = numbers[n]; // ascending the card number from the number string array
                String card = color +number; // Which turns into the card file name as card name
                int cardPic = getResources().getIdentifier(card,"drawable",getPackageName()); // Convert this into drawable number object
                cards.put(card,cardPic); // Finally pushing the Hashmap with the card name as key and card drawable object as value
            }
        }
    }
    private void difficultyDecider( int difficulty){
        if(difficulty == 0){ // Easy
            playCards = 16;
            column = 4;
            row =4;
            cardWidth=300;cardHeight=400; // Set the card image size for render
            cardX =350;cardY=400;
            playNumber=8;// Two color are played
        } else if(difficulty == 3){ // Arranged
            playCards =36;
            column = 6;
            row = 6;
            cardWidth=200;cardHeight=270;
            cardX =250;cardY=280;
            playNumber=9; // 4 colors is played
        } else if(difficulty == 1 || difficulty == 2){
            playCards = 24;
            column = 4;
            row = 6;
            cardWidth=220;cardHeight=400;
            cardX =225;cardY=400;
            playNumber=12;// Two color are played
            if(difficulty ==2){
                playNumber /= 2; // 4 colors is played
            }
        }
    }
    private void buildCard(int difficulty){
        difficultyText.setText(difficulties[difficulty]); // Set the difficulty
        // Set all the attribute related the deck to zero for initialize
        playableCards = new ArrayList<String>(); //Playable Card container declared as ArrayList
        String[] coinside = null; // If the difficulty is either Easy or Normal, only one color is play, so we need to declare it first as null.
        if(difficulty<2){
            // Run the line below to determine the play color randomly if the difficult is Easy or Normal
            // The combo will be either "Club and Spade" or "Diamond and Heart"
            coinside = new Random().nextBoolean()?new String[]{"c","s"}:new String[]{"d","h"};
        }

        // Then ,set all attribute of the deckboard cards regarding to the difficulty selected.
        // The layout would be different ,as well as the playable card numbers , which will be randomly picked later
        difficultyDecider(difficulty);
        // This will show how many cards player is playing by sending Toast.
        Toast.makeText(this,"You're playing "+difficulties[difficulty]+" mode, playing "+String.valueOf(playCards) + " cards.",Toast.LENGTH_SHORT).show();

        cardImages = new ImageView[playCards]; //declare playableCards to cardImages as ArrayList
        curcardspos = new ArrayList<int[]>(); // Store the position of the card anchor here

        ArrayList<String> playableNumbers = new ArrayList<String>(Arrays.asList(numbers));
        solvedCards = new HashMap<Integer, String>();

        for(int rn = 0;rn<(numbers.length-playNumber);rn++){
            playableNumbers.remove((int)Math.round(Math.random()*(playableNumbers.size()-1)));
        }
        for(int i = 0 ; i < playCards;i++) {

            cardImages[i] = new ImageView(this);
            Bitmap bmp = generateBitmap(R.drawable.purple_back);
            cardImages[i].setImageBitmap(bmp);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
            double currow = Math.floor(i / row);
            params.leftMargin = cardX * (i % row);
            params.topMargin = cardY * (int) currow;
            curcardspos.add(new int[]{cardX * (i % row), cardY * (int) currow});
            deckboard.addView(cardImages[i], params);

            solvedCards.put(i,"Unsolved");
        }
        if(coinside != null){
            for(String c:coinside){
                for(int k = 0;k<(playableNumbers.size());k++){
                    Log.d("indexxx",String.valueOf(k));
                    playableCards.add(c+playableNumbers.get(k));
                }
            }
        } else {
            for(String c:colors){
                for(int k = 0;k<(playableNumbers.size());k++){
                    Log.d("indexxx",String.valueOf(k));
                    playableCards.add(c+playableNumbers.get(k));
                }
            }
        }
        Collections.shuffle(playableCards);
    }
    private void tick(){
        s +=1;
        if(s==60){
            m+=1;s=0;
        }
        String mt = m>9? Integer.toString(m):"0"+Integer.toString(m);
        String st = s>9? Integer.toString(s):"0"+Integer.toString(s);
        timer.setText(mt+":"+st);
    }
    private void tickTimer(){
        timerTicker = new Timer();
        TimerTask tasks = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tick();
                    }
                });
            }
        };
        timerTicker.scheduleAtFixedRate(tasks,0,1000);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.finishAbout:
                finish();
                break;
        }
    }
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setMessage("Are you sure you want to leave the game?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deckboard.removeAllViewsInLayout();
                finish();
            }
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog ad = adb.create();
        ad.setTitle("Quit Game");
        ad.show();
    }
    private Bitmap generateBitmap(int drawable){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bmp;
        bmp = BitmapFactory.decodeResource(getResources(), drawable, options);
        return bmp;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventAction = event.getAction();
        cx = (int) event.getX();
        cy = (int) event.getY();
        switch (eventAction){
            case MotionEvent.ACTION_DOWN:
                checkCard();
                break;
            default:
                break;
        }
        return true;
    }
    private void checkCard(){
        for(int i = 0;i<curcardspos.size();i++){
            if(posChecker(i)){
                if(solvedCards.get(i) == "Solved"){
                    return;
                }
                String thisCard = playableCards.get(i);
                int curDrawable = cards.get(thisCard);
                Bitmap bmp = generateBitmap(curDrawable);
                cardImages[i].setImageBitmap(bmp);
                if(revealCard1 == null ){
                    revealCard1 = thisCard;
                } else if(revealCard2 == null){
                    revealCard2 = thisCard;
                    if(revealCard1 != null && revealCard2 != null){
                        deckboard.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                    }
                    Timer cardTime = new Timer();
                    TimerTask tasks = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardMatching();
                                }
                            });
                        }
                    };
                    cardTime.schedule(tasks,500);
                }
            }
        }
    }
    private boolean posChecker(int i){
      return cx>=curcardspos.get(i)[0] && cx<=curcardspos.get(i)[0]+cardWidth && cy>=curcardspos.get(i)[1] && cy<=curcardspos.get(i)[1]+cardHeight;
    }
    private void cardMatching(){
        if(solvedCards.get(playableCards.indexOf(revealCard1)) == "Solved" || solvedCards.get(playableCards.indexOf(revealCard2)) == "Solved"){
            revealCard1 = null;
            revealCard2 = null;
            deckboard.setOnTouchListener(this);
            return;
        }
        String card1Color = revealCard1.substring(0,1);
        String card1Number = revealCard1.substring(1);
        String card2Color = revealCard2.substring(0,1);
        String card2Number =revealCard2.substring(1);

        Boolean numberMatch = card1Number.equals(card2Number);
        Boolean blackMatch = (card1Color.equals("s") && card2Color.equals("c")) || (card1Color.equals("c") && card2Color.equals("s") );
        Boolean redMatch = (card1Color.equals("d") && card2Color.equals("h")) || (card1Color.equals("h") && card2Color.equals("d") );
        if(numberMatch && (blackMatch || redMatch)){
            Animation fo = new AlphaAnimation(1.0f,0.0f);
            fo.setDuration(1000);
            cardImages[playableCards.indexOf(revealCard1)].startAnimation(fo);
            cardImages[playableCards.indexOf(revealCard2)].startAnimation(fo);

            cardImages[playableCards.indexOf(revealCard1)].setVisibility(View.INVISIBLE);
            cardImages[playableCards.indexOf(revealCard2)].setVisibility(View.INVISIBLE);

            solvedCards.put(playableCards.indexOf(revealCard1),"Solved");
            solvedCards.put(playableCards.indexOf(revealCard2),"Solved");


            checkEndGame();
        } else {
            Bitmap bmp = generateBitmap(R.drawable.purple_back);
            cardImages[playableCards.indexOf(revealCard1)].setImageDrawable(getResources().getDrawable(R.drawable.purple_back));
            cardImages[playableCards.indexOf(revealCard2)].setImageDrawable(getResources().getDrawable(R.drawable.purple_back));
        }
        revealCard1 = null;
        revealCard2 = null;
        deckboard.setOnTouchListener(this);
    }
    private void checkEndGame(){
        Collection<String> solvedlist = solvedCards.values();
        if(Collections.frequency(solvedlist,"Unsolved") == 0 ){
            timerTicker.cancel();
            timerResult.setText(timer.getText());
            difficultyResult.setText(difficultyText.getText());
            congratsLayout.setVisibility(View.VISIBLE);
        }

    }
}