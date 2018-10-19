package br.com.ratske.workout.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.ratske.workout.R;
import br.com.ratske.workout.model.Workout;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.MyViewHolder> {
    private List<Workout> workouts;
    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemLista = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_workout, viewGroup, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Workout workout = workouts .get(i);
        myViewHolder.name.setText(workout.getName());
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);

        }
    }
}
