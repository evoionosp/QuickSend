package com.devshubhpatel.quicksend;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import java.text.SimpleDateFormat;
import java.util.List;

import io.realm.Realm;

import static com.devshubhpatel.quicksend.MainActivity.realm;

/**
 * Created by patel on 15-07-2017.
 */

class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<Reminder> reminderList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title,mobile,when,remaining;
        private SingleSelectToggleGroup sstg;


        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            mobile = (TextView) view.findViewById(R.id.card_mob);
            when = (TextView) view.findViewById(R.id.card_datetime);
            remaining = (TextView) view.findViewById(R.id.card_remaining);
            sstg = (SingleSelectToggleGroup) view.findViewById(R.id.card_group_choices);

        }
    }


    public ReminderAdapter(Activity mActivity, Context mContext, List<Reminder> reminderList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.reminderList = reminderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_reminder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Reminder reminder = reminderList.get(position);
        holder.title.setText(reminder.getrTitle());
        holder.mobile.setText("+"+reminder.getrMobile());
        holder.when.setText(displayTime(reminder.getrTime()));
        holder.remaining.setText(displayRemaining(reminder.getrTime()));
        holder.sstg.check(reminder.getrType());

        holder.sstg.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                reminder.setrType(checkedId);

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        bgRealm.copyToRealmOrUpdate(reminder);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mActivity, "Saved !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private String displayTime(long timeticks){

        return  new SimpleDateFormat("hh:mm a").format(timeticks) + " | " + new SimpleDateFormat("dd/MM/yyyy").format(timeticks);

        //return "12:35 PM | 12/05/17";
    }

    private String displayRemaining(long timeticks){
        return "(1Day 15Hr Remaining)";
    }



    @Override
    public int getItemCount() {
        return reminderList.size();
    }
}