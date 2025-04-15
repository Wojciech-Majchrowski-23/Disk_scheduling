package src;

import java.util.ArrayList;

public class ProcessesGenerator {

    public ProcessesGenerator() {}

    public static ArrayList<Process> generateProcesses(int numberOfProcesses, int maxArrivalTime) {
        ArrayList<Process> processes = new ArrayList<>();

        int arrivalTime, position;

//        for(int i = 0; i < numberOfProcesses; i++){
//            arrivalTime = (int)(Math.random() * maxArrivalTime);
//            position = (int)(Math.random() * 1000);
//            processes.add(new Process(i, arrivalTime, position));
//        }

        ///special verison

        int position1, position2;
        for(int i = 0; i < numberOfProcesses; i++){

            arrivalTime = (int)(Math.random() * maxArrivalTime);
            position1 = (int)(Math.random() * 100);
            position2 = (int)(Math.random() * 100) + 900;

            if(position1 % 2 == 0){
                position = position1;
            }else{
                position = position2;
            }
            processes.add(new Process(i, arrivalTime, position));
        }

        return processes;

    }


}
