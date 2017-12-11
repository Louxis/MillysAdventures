package com.example.notaj.millyadventure;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * This class sets up the post-minigame frame;
 */

public class GameScoreView extends View{
    private ConstraintLayout frame;
    private ImageView carrot1;
    private ImageView carrot2;
    private ImageView carrot3;
    private ImageButton homeButton;
    private ImageButton nextButton;
    private Animation fadeInAnimation;
    private int score;

    /**
     * This constructor initializes the post-minigame frame;
     * @param currentActivity the current activity;
     * @param score the final score of the current activity;
     * @param nextLevel the next level's class;
     */
    public GameScoreView(Activity currentActivity, int score, Class nextLevel) {
        super(currentActivity);
        final Activity CURRENT_ACTIVITY = currentActivity;
        final Class NEXT_LEVEL = nextLevel;
        this.frame = (ConstraintLayout)currentActivity.findViewById(R.id.score_frame);
        this.carrot1 = (ImageView)currentActivity.findViewById(R.id.carrot1);
        this.carrot2 = (ImageView)currentActivity.findViewById(R.id.carrot2);
        this.carrot3 = (ImageView)currentActivity.findViewById(R.id.carrot3);

        this.homeButton = (ImageButton)currentActivity.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENT_ACTIVITY.startActivity(new Intent(CURRENT_ACTIVITY, LevelSelect.class));
                CURRENT_ACTIVITY.finish();
            }
        });

        this.nextButton = (ImageButton)currentActivity.findViewById(R.id.next_level);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CURRENT_ACTIVITY.startActivity(new Intent(CURRENT_ACTIVITY, NEXT_LEVEL));
                    CURRENT_ACTIVITY.finish();
                }
            });

        this.fadeInAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.enter_from_bottom);

        this.score = score;
        manageScore();

        this.frame.setVisibility(VISIBLE);
        this.frame.startAnimation(fadeInAnimation);
    }

    /**
     * This manages the amount of carrots that the player will be shown on the frame;
     */
    public void manageScore(){
        switch (this.score){
            case 1:
                carrot1.setVisibility(INVISIBLE);
                carrot2.setVisibility(VISIBLE);
                carrot3.setVisibility(INVISIBLE);
                break;
            case 2:
                carrot1.setVisibility(VISIBLE);
                carrot2.setVisibility(INVISIBLE);
                carrot3.setVisibility(VISIBLE);
                break;
            case 3:
                carrot1.setVisibility(VISIBLE);
                carrot2.setVisibility(VISIBLE);
                carrot3.setVisibility(VISIBLE);
                break;
        }
    }
}
