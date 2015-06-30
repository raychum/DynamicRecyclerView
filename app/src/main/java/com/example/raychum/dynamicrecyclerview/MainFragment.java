package com.example.raychum.dynamicrecyclerview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by raychum on 30/6/15.
 */
public class MainFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private MainAdapter adapter;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainAdapter();
        recyclerView.setAdapter(adapter);
        editText = (EditText) rootView.findViewById(R.id.edittext);
        for (int i = 0; i < 10; i++){
            adapter.addItem(new Item(i));
        }
        rootView.findViewById(R.id.button_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    final Intent intent = new Intent(ChildFragment.ACTION_VISIBILITY);
                    intent.putExtra(ChildFragment.VISIBILITY_KEY, true);
                    intent.putExtra(ChildFragment.POSITION_KEY, Integer.valueOf(editText.getText().toString()));
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChildFragment.ACTION_VISIBILITY);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getIntExtra(ChildFragment.POSITION_KEY, -1);
                switch (intent.getAction()) {
                    case ChildFragment.ACTION_VISIBILITY:
                        boolean visible = intent.getBooleanExtra(ChildFragment.VISIBILITY_KEY, false);
                        if (visible) {
                            adapter.showItemAt(position);
                        } else {
                            adapter.hideItemAt(position);
                        }
                        break;
                }
            }
        },intentFilter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class MainAdapter extends RecyclerView.Adapter<ChildViewHolder> {

        private final ArrayList<Item> itemList = new ArrayList<>();
        private final ArrayList<Item> itemDisplayList = new ArrayList<>();

        @Override
        public ChildViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeId) {
            Log.d("Ray", "onCreateViewHolder typeId=" + typeId);
            Item item;
            for (int i = 0; i < itemDisplayList.size(); i++) {
                item = itemDisplayList.get(i);
                if (item.getPosition() == typeId) {
                    return item.getViewHolder(viewGroup);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ChildViewHolder viewHolder, int position) {
            Log.d("Ray", "onBindViewHolder position=" + position);
            if (itemDisplayList.get(position).getFragment() == null) {
                itemDisplayList.get(position).initFragment(getChildFragmentManager(), position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return itemDisplayList.get(position).getPosition();
        }

        @Override
        public int getItemCount() {
            return itemDisplayList.size();
        }

        public void addItem(Item item) {
            itemList.add(item);
            itemDisplayList.add(item);
            notifyDataSetChanged();
        }

        public void clearAllItems() {
            itemList.clear();
            itemDisplayList.clear();
            notifyDataSetChanged();
        }

        public void hideItemAt(int position) {
            if (itemList.size() > 0) {
                itemDisplayList.clear();
                for (Item item : itemList) {
                    if (item.getPosition() == position) {
                        item.setVisible(false);
                    }
                    if (item.isVisible()) {
                        itemDisplayList.add(item);
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void showItemAt(int position) {
            if (itemList.size() > 0) {
                itemDisplayList.clear();
                for (Item item : itemList) {
                    if (item.getPosition() == position) {
                        item.setVisible(true);
                    }
                    if (item.isVisible()) {
                        itemDisplayList.add(item);
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    public class Item {
        private final int containerId;
        private final int position;
        private boolean isVisible = true;
        private ChildViewHolder viewHolder;
        private ChildFragment childFragment;

        public Item(int position) {
            Log.d("Ray", "position=" + position);
            this.position = position;
            containerId = position + 1;
        }

        public int getPosition() {
            return position;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setVisible(boolean isVisible) {
            this.isVisible = isVisible;
        }

        public ChildViewHolder getViewHolder(ViewGroup viewGroup) {
            if (viewHolder == null) {
                final View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.fragment_container, viewGroup, false);
                v.setId(containerId);
                viewHolder = new ChildViewHolder(v);
            }
            return viewHolder;
        }

        public void initFragment(FragmentManager fm, int position) {
            if (viewHolder != null && viewHolder.getRootView().findViewById(containerId) != null) {
                childFragment = ChildFragment.newInstance(position);
                fm.beginTransaction()
                        .replace(containerId,
                                childFragment).commit();
            }
        }

        public ChildFragment getFragment() {
            return childFragment;
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private final View rootView;

        public ChildViewHolder(View v) {
            super(v);
            rootView = v;
        }

        public View getRootView() {
            return rootView;
        }
    }
}
