package com.example.timer.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.timer.App;
import com.example.timer.R;
import com.example.timer.model.Exercise;
import com.example.timer.model.Training;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private SortedList<Exercise> sortedList;

    public ExerciseAdapter(){
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
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExerciseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }
    public void setItems(List<Exercise> exercises){
        sortedList.replaceAll(exercises);
    }

    static public class ExerciseViewHolder extends RecyclerView.ViewHolder{

        EditText exerciseName;
        EditText exerciseTime;
        Button deleteButton;
        Button saveButton;
        Exercise exercise;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteButton = itemView.findViewById(R.id.delete_exercise);
            saveButton = itemView.findViewById(R.id.save_exercise);
            exerciseName = itemView.findViewById(R.id.exercise_text);
            exerciseTime = itemView.findViewById(R.id.exercise_time);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().getExerciseDao().delete(exercise);
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exercise.name = exerciseName.getText().toString();
                    exercise.time = Integer.parseInt(exerciseTime.getText().toString());
                    App.getInstance().getExerciseDao().update(exercise);
                }
            });
        }
        public void bind(Exercise exercise){
            this.exercise = exercise;
            exerciseName.setText(exercise.name);
            exerciseTime.setText(String.valueOf(exercise.time));
        }
    }








}
