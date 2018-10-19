package br.com.ratske.workout.model;

public class Exercise {
    private String name;
    private int repetitions;
    private String workout;

    public Exercise() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public String getWorkout() {
        return workout;
    }

    public void setWorkout(String workout) {
        this.workout = workout;
    }
}
