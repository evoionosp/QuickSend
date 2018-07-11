package com.devshubhpatel.quicksend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.devshubhpatel.quicksend.MainActivity.realm;

/**
 * Created by patel on 08-07-2017.
 */

public class ReminderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootview;
    String TAG = "ReminderFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    List<Reminder> reminderList;

    ReminderAdapter adapter;

    SwipeRefreshLayout swiperefresh;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView);
        reminderList = new ArrayList<>();
        reminderList = realm.where(Reminder.class).findAll();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swiperefresh = (SwipeRefreshLayout) rootview.findViewById((R.id.swiperefresh));
        swiperefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        swiperefresh.setOnRefreshListener(this);
        adapter = new ReminderAdapter(getActivity(),getActivity().getApplicationContext(), reminderList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(),EditReminderActivity.class);
                        intent.putExtra("Reminder_ID",reminderList.get(position).get_id());
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        return rootview;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "List : "+reminderList.size()+" Adapt : "+adapter.getItemCount(), Toast.LENGTH_SHORT).show();
        reminderList = realm.where(Reminder.class).findAll();
        adapter.notifyDataSetChanged();
        swiperefresh.setRefreshing(false);
    }




}
