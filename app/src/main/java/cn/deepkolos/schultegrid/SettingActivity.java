package cn.deepkolos.schultegrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by deepkolos on 2018/3/23.
 */

public class SettingActivity extends Activity {
    private Button sizeBtn;
    private Button difficultyBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Const.init(this);

        // set current setting
        sizeBtn = findViewById(R.id.size_btn);
        difficultyBtn = findViewById(R.id.difficulty_btn);

        SharedPreferences sp = this.getSharedPreferences(Const.SETTING, MODE_PRIVATE);
        String currSize = sp.getString(Const.SIZE, "");
        String currDifficulty = sp.getString(Const.DIFFICULTY, "");

        if (currDifficulty.equals("") && currSize.equals("")) {
            currDifficulty = Const.difficulties[1];
            currSize = Const.sizes[1];

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Const.DIFFICULTY, currDifficulty);
            editor.putString(Const.SIZE, currSize);
            editor.apply();
        }

        sizeBtn.setText(currSize);
        difficultyBtn.setText(currDifficulty);

        Button historyBtn = findViewById(R.id.history_btn);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        Button aboutBtn = findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("关于")
                        .setMessage("一个简洁的舒尔特方格.\ndeepkolos with ungatz.")
                        .show();
            }
        });
    }

    public void onChooseDifficulty(View view) {
        makeOptions(getString(R.string.setting_difficulty), Const.DIFFICULTY, Const.difficulties, new OptionCallBack() {
            @Override
            public void call(String result) {
                difficultyBtn.setText(result);

                if (result.equals(getString(R.string.difficulty_hard)))
                    Toast.makeText(SettingActivity.this, "按错罚时100ms", Toast.LENGTH_SHORT).show();
                if (result.equals(getString(R.string.difficulty_easy)))
                    Toast.makeText(SettingActivity.this, "按对有反馈", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onChooseSize(View view) {
        makeOptions(getString(R.string.setting_size), Const.SIZE, Const.sizes, new OptionCallBack() {
            @Override
            public void call(String result) {
                sizeBtn.setText(result);
            }
        });
    }

    public void onStart (View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    private void makeOptions (String title, final String type, final String[] options, final OptionCallBack cb) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title).setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sp = SettingActivity.this.getSharedPreferences(Const.SETTING, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(type, options[i]);
                editor.apply();
                cb.call(options[i]);
            }
        });

        alertDialog.show();
    }

    private interface OptionCallBack {
        void call (String result);
    }
}
