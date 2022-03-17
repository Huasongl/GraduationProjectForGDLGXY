package com.gdlgxy.internshipcommunity.module.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gdlgxy.internshipcommunity.R;
import com.gdlgxy.internshipcommunity.module.find.StatusBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserBehaviorListActivity extends AppCompatActivity {
    public static final int BEHAVIOR_FAVORITE = 0;
    public static final int BEHAVIOR_HISTORY = 1;

    public static final String KEY_BEHAVIOR = "behavior";

    public static void startBehaviorListActivity(Context context, int behavior) {
        Intent intent = new Intent(context, UserBehaviorListActivity.class);
        intent.putExtra(KEY_BEHAVIOR, behavior);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_list);

        UserBehaviorListFragment fragment = UserBehaviorListFragment.newInstance(getIntent().getIntExtra(KEY_BEHAVIOR, BEHAVIOR_FAVORITE));
        getSupportFragmentManager().beginTransaction().add(R.id.contanier, fragment, "userBehavior").commit();
    }
}
