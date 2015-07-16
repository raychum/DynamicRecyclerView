package com.example.raychum.dynamicrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by raychum on 30/6/15.
 */
public class ChildFragment extends Fragment {

    public static final String ACTION_VISIBILITY = "ACTION_VISIBILITY";
    public static final String POSITION_KEY = "POSITION_KEY";
    public static final String VISIBILITY_KEY = "VISIBILITY_KEY";
    private View rootView;
    private TextView textView;
    private int position;

    public ChildFragment() {
        // Required empty public constructor
    }

    public static ChildFragment newInstance(int position) {
        ChildFragment fragment = new ChildFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_child, container, false);
        textView = (TextView) rootView.findViewById(R.id.textview);
        position = getArguments().getInt(POSITION_KEY, -1);
        textView.setText(String.valueOf(position));
        rootView.findViewById(R.id.button_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ACTION_VISIBILITY);
                intent.putExtra(VISIBILITY_KEY, false);
                intent.putExtra(POSITION_KEY, position);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Intent intent = new Intent(ACTION_VISIBILITY);
                        intent.putExtra(VISIBILITY_KEY, true);
                        intent.putExtra(POSITION_KEY, position);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                }, 5000);
            }
        });
        return rootView;
    }
}