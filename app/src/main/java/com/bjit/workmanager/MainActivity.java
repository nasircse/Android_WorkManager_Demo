package com.bjit.workmanager;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class MainActivity extends AppCompatActivity {

    // Special notes
/*    = Test with minimum time delay 15 minutes
      = Need minimum sdk 9
      = Will work at ideal word scenery*/

    // Variables and constance
    private Activity mActivity;

    private OneTimeWorkRequest simpleRequest;
    private PeriodicWorkRequest periodicWorkRequest;
    private String TAG_ONE_TIME_WORK = "simple_work";
    private String TAG_PERIODIC_WORK = "periodic_work";

    // Views
    private TextView tvWorkStatus;
    private Button btnSimpleWork, btnCancelSimpleWork, btnPeriodicWork, btnCancelPeriodicWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initViews();
        initListener();
    }

    private void initVariables() {
        mActivity = MainActivity.this;
    }

    private void initViews() {
        setContentView(R.layout.activity_main);

        tvWorkStatus = findViewById(R.id.tv_work_status);
        btnSimpleWork = findViewById(R.id.btn_simple_work);
        btnCancelSimpleWork = findViewById(R.id.btn_cancel_simple_work);
        btnPeriodicWork = findViewById(R.id.btn_periodic_work);
        btnCancelPeriodicWork = findViewById(R.id.btn_cancel_periodic_work);
    }

    private void initListener() {
        btnSimpleWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSimpleWork(15);
                Toast.makeText(MainActivity.this, "Start simple task", Toast.LENGTH_SHORT).show();
                // run work observer
                runWorkObserver();
            }
        });

        btnCancelSimpleWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID workId = simpleRequest.getId();

                //WorkManager.getInstance().cancelAllWorkByTag(TAG_ONE_TIME_WORK);
                WorkManager.getInstance().cancelWorkById(workId);
            }
        });

        btnPeriodicWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executePeriodicWork(15, 3);
                Toast.makeText(MainActivity.this, "Start periodic task", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelPeriodicWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID workId = periodicWorkRequest.getId();

                WorkManager.getInstance().cancelAllWorkByTag(TAG_PERIODIC_WORK);
                WorkManager.getInstance().cancelWorkById(workId);
            }
        });
    }

    private void executeSimpleWork(int initialDelayInMin) {
        Data data = new Data.Builder()
                .putString(MyWorker.EXTRA_TITLE, "Message from Activity!")
                .putString(MyWorker.EXTRA_TEXT, "Hi! I have come from activity.")
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        simpleRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .addTag(TAG_ONE_TIME_WORK)
                .setInputData(data)
                .setConstraints(constraints)
                .setInitialDelay(initialDelayInMin, TimeUnit.MINUTES)
                .build();
        // start work
        WorkManager.getInstance().enqueue(simpleRequest);
    }

    private void executePeriodicWork(int repeatInterval, int flexInterval) {
        Data data = new Data.Builder()
                .putString(MyWorker.EXTRA_TITLE, "Message from Activity!")
                .putString(MyWorker.EXTRA_TEXT, "Hi! I have come from activity.")
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, repeatInterval, TimeUnit.MINUTES, flexInterval, TimeUnit.MINUTES)
                .addTag(TAG_PERIODIC_WORK)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        // start work
        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }

    private void runWorkObserver(){
        // get one time work status
        WorkManager.getInstance().getStatusById(simpleRequest.getId())
                .observe(this, new Observer<WorkStatus>() {
                    @Override
                    public void onChanged(@Nullable WorkStatus workStatus) {
                        if (workStatus != null) {
                            tvWorkStatus.append("SimpleWorkRequest: " + workStatus.getState().name() + "\n");
                        }

                        if (workStatus != null && workStatus.getState().isFinished()) {

                            Toast.makeText(mActivity, "Work finished", Toast.LENGTH_SHORT).show();
                            // start task again if you want
                            //executeSimpleWork(15);
                        }
                    }
                });
    }
}