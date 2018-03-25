package cn.deepkolos.schultegrid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by deepkolos on 2018/3/23.
 */

public class PlayActivity extends Activity {
    private GridLayout gridLayout;
    private int colCount;
    private GirdItemClick girdItemClick = new GirdItemClick();
    private String difficulty;
    private Long startTime;
    private int bill;
    private int nextExpected = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);

        SharedPreferences sp = this.getSharedPreferences(Const.SETTING, MODE_PRIVATE);
        String currSize = sp.getString(Const.SIZE, "");
        String currDifficulty = sp.getString(Const.DIFFICULTY, "");
        colCount = Integer.parseInt(currSize.substring(0, 1));
        difficulty = currDifficulty;

        gridLayout = findViewById(R.id.grid);
    }

    private void initGrid (int colCount) {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(colCount);
        gridLayout.setVisibility(View.VISIBLE);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        RelativeLayout layout = findViewById(R.id.root_layout);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int layoutWidth = layout.getWidth();
        int itemSquareLen = (layoutWidth - (metrics.densityDpi/160) * colCount*2)/colCount;

        for (int i = 0; i < colCount; i++){
            for (int j = 0; j < colCount; j++) {
                Random random = new Random();
                int index;
                do {
                    index = random.nextInt(colCount*colCount+1);
                } while (list.contains(index));
                list.add(index);

                String indexStr = ""+index;

                Button btn = (Button) Tpl.instance(this, R.layout.tpl, R.id.grid_item_btn);
                ViewGroup.LayoutParams layoutParams =  btn.getLayoutParams();
                layoutParams.height = itemSquareLen;
                layoutParams.width = itemSquareLen;
                btn.setLayoutParams(layoutParams);
                btn.setText(indexStr);
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 37-(colCount-3)*5);
                btn.setOnClickListener(girdItemClick);

                gridLayout.addView(btn);
            }
        }
    }

    public class GirdItemClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button btn = (Button) view;
            String indexStr = (String) btn.getText();
            int index = Integer.parseInt(indexStr);

            if (nextExpected == index) {
                nextExpected++;

                if (nextExpected == colCount*colCount+1) {
                    onFinish();
                }

                if (difficulty.equals(getString(R.string.difficulty_easy))){
                    btn.setBackgroundColor(Color.GRAY);
                }
            }
            else {
                if (difficulty.equals(getString(R.string.difficulty_hard))) {
                    bill += 100;
                }
            }
        }
    }

    public void onReady(View view) {
        view.setVisibility(View.GONE);
        initGrid(colCount);
        startTime = System.currentTimeMillis();
        bill = 0;

        TextView result = findViewById(R.id.result);
        result.setVisibility(View.GONE);

    }

    public void onFinish () {
        Button btn = findViewById(R.id.ready_btn);

        btn.setVisibility(View.VISIBLE);
        btn.setText(getString(R.string.again));
        gridLayout.setVisibility(View.GONE);
        long endTime = System.currentTimeMillis();
        float ms = (float)(endTime - startTime + bill)/1000;

        TextView result = findViewById(R.id.result);
        result.setText(ms+"s");
        result.setVisibility(View.VISIBLE);
        nextExpected = 1;

        DB db = DB.getInstance(this);
        db.record(ms);
    }
}
