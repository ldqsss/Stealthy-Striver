package com.mediading.stealthystriver.ui.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.mediading.stealthystriver.R;
import com.mediading.stealthystriver.StealthyStriverApplication;
import com.mediading.stealthystriver.databinding.RankFragmentBinding;
import com.mediading.stealthystriver.view.LoadingDialog;
import com.mediading.stealthystriver.viewmodel.BaseViewModel;
import com.mediading.stealthystriver.viewmodel.RankFragmentViewModel;

@AndroidEntryPoint
public class RankFragment extends BaseFragment {

    private RankFragmentViewModel rankFragmentViewModel;
    private RankFragmentBinding dataBinding;
    private Boolean active = false;
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建viewmodel
        rankFragmentViewModel = new ViewModelProvider(this).get(RankFragmentViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.rank_fragment,container,false);
        dataBinding.setFocus(rankFragmentViewModel);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataBinding.timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rankFragmentViewModel.getFocusProgress().setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initView();
    }

    private void initView(){

        dataBinding.startButton.setOnClickListener(this::startClock);

        rankFragmentViewModel.getDataStatus().observe(getViewLifecycleOwner(), new Observer<BaseViewModel.DataStatus>() {
            @Override
            public void onChanged(BaseViewModel.DataStatus dataStatus) {
                toastShort(dataStatus.getMsg());
            }
        });

        rankFragmentViewModel.getFocusTimeLeft().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                toastLong(s);
            }
        });

        rankFragmentViewModel.getFocusTime().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dataBinding.timerText.setText(s);
            }
        });

        rankFragmentViewModel.getFocusProgress().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i(TAG,"getFocusProgress().observe");
//                toastShort("ddd"+integer);
            }
        });
    }


    public void resetTimer(){
        rankFragmentViewModel.setFocusProgress(rankFragmentViewModel.defaultFocusProgress);
        rankFragmentViewModel.saveCurrentFocusTime();
        countDownTimer.cancel();
        dataBinding.startButton.setText("START");
        dataBinding.timeBar.setEnabled(true);
        active = false;
    }

    public void startClock(View view){
        if (!active) {
            active = true;
            dataBinding.timeBar.setEnabled(false);
            rankFragmentViewModel.getFocusProgress().setValue(dataBinding.timeBar.getProgress());
            dataBinding.startButton.setText("RESET");
            Log.i("Button", "Pressed");
            countDownTimer = new CountDownTimer(dataBinding.timeBar.getProgress() * 1000L + 100, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    rankFragmentViewModel.getFocusProgress().setValue((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    resetTimer();
                    MediaPlayer play = MediaPlayer.create(StealthyStriverApplication.getContext(), R.raw.alarm);
                    play.start();
                }
            }.start();
        } else {
            resetTimer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rankFragmentViewModel.saveCurrentFocusTime();
        Log.i(TAG,"on Destory() sssssssssssssssssssss");
    }
}