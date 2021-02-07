package com.example.timer.activities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.timer.R;
import com.example.timer.model.Training;

import java.util.List;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    private SortedList<Training> sortedList;

    public TrainingAdapter(){
        //список для сообщения об изменениях в адаптер
        sortedList = new SortedList<>(Training.class, new SortedList.Callback<Training>() {
            @Override
            public int compare(Training o1, Training o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Training oldItem, Training newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Training item1, Training item2) {
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
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrainingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<Training> trainings){
        sortedList.replaceAll(trainings);
    }

    static class TrainingViewHolder extends RecyclerView.ViewHolder{

        TextView trainingText;
        Button deleteButton;
        Training training;


        public TrainingViewHolder(@NonNull View itemView){
            super(itemView);
            trainingText = itemView.findViewById(R.id.training_text);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    TrainingDetailActivity.start((Activity) itemView.getContext(), training);
                }
            });


        }

        public void bind(Training training){
            this.training = training;
            trainingText.setText(training.name);
            itemView.setBackgroundColor(training.line_colour);
        }
    }
}
