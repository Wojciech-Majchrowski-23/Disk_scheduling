package src;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        int numberOfDataSets = 5;

        ArrayList<Process>[] processChains = new ArrayList[numberOfDataSets];

        for(int i = 0; i < numberOfDataSets; i++){
            processChains[i] = ProcessesGenerator.generateProcesses(50, 300);
        }

        DISK disk = new DISK();

        for(int i = 0; i < numberOfDataSets; i++){

//            System.out.println("[Data_number: " + i + " ]");
//            for(int j = 0; j < processChains[i].size(); j++){
//                System.out.println("[ Process position: " + processChains[i].get(j).getPosition() + " ] , [ Arrival time: " + processChains[i].get(j).getArrivalTime() + " ]");
//            }
//            System.out.println();

            System.out.printf("%30s %n","[head movements: " + disk.firstComeFirstServed(deepCopy(processChains[i])) + "]");
            System.out.printf("%30s %n","[head movements: " + disk.shortestSeekTimeFirst(deepCopy(processChains[i])) + "]");
            System.out.printf("%30s %n","[head movements: " + disk.scan(deepCopy(processChains[i])) + "]");
            System.out.printf("%30s %n","[head movements: " + disk.cScan(deepCopy(processChains[i])) + "]");
            System.out.printf("%30s %n","[head movements: " + disk.scanWithEDF(deepCopy(processChains[i])) + "]");
            System.out.printf("%30s %n","[head movements: " + disk.cScanWithFDscan(deepCopy(processChains[i])) + "]");

            System.out.println();
        }
    }

    public static ArrayList<Process> deepCopy(ArrayList<Process> original) {

        ArrayList<Process> copy = new ArrayList<>();

        for (Process p : original) {
            copy.add(p.clone());
        }
        return copy;
    }
}