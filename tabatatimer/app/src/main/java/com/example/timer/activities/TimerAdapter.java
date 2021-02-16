package com.example.timer.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.timer.R;
import com.example.timer.model.Exercise;

import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {

    private SortedList<Exercise> sortedList;

    public TimerAdapter(){
        sortedList = new SortedList<>(Exercise.class, new SortedList.Callback<Exercise>() {
            @Override
            public int compare(Exercise o1, Exercise o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Exercise oldItem, Exercise newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Exercise item1, Exercise item2) {
                return item1.uid == item2.uid;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intimer_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }
    public void setItems(List<Exercise> exercises){
        sortedList.replaceAll(exercises);
    }


    static public class TimerViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName;
        TextView exerciseTime;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseTime = itemView.findViewById(R.id.exercise_time_adapter);
        }
        public void bind(Exercise exercise) {
            exerciseName.setText(exercise.name);
            exerciseTime.setText(String.valueOf(exercise.time));
        }
    }

}
