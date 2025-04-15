package src;

import java.util.*;

public class DISK {

    private final int diskSize = 1000;
    private int localTime, headPosition;


    public DISK() {}

    public int firstComeFirstServed(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-30s", "[First_Come_First_Served]");
        return processingForFCFS(processes, diskQueue);
    }

    public int shortestSeekTimeFirst(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-30s","[Shortest_Seek_Time_First]");
        return processingForSSTF(processes, diskQueue);
    }

    public int scan(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-30s","[Scan]");
        return processingForScan(processes, diskQueue);
    }

    public int scanWithEDF(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-30s","[Scan with EDF]");
        return processingForScanWithEDF(processes, diskQueue);
    }

    public int cScan(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-30s","[C-Scan]");
        return processingForCScan(processes, diskQueue);
    }

    public int cScanWithFDscan(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-30s","[C-Scan with FD-scan]");
        return processingForCScanWithFDscan(processes, diskQueue);
    }


    public int processingForFCFS(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int headMovements = 0;
        System.out.printf("%-10s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        int target = -1, direction = 0;

        while(!isEveryProcessFinished(processes)){

            if(!diskQueue.isEmpty()){

                target = diskQueue.peek().getPosition();

                if (headPosition != target) {
                    direction = Integer.compare(target, headPosition);

                    while (headPosition != target) {
                        headPosition += direction;
                        headMovements++;
                        diskUpdate(processesQueue, diskQueue);
                    }
                }

                Process completed = diskQueue.poll();
                if (completed != null) {
                    completed.setFinished(true);
                }

            }
            diskUpdate(processesQueue, diskQueue);

        }

        return headMovements;
    }

    public int processingForSSTF(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int headMovements = 0;
        System.out.printf("%-10s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        while (!isEveryProcessFinished(processes)) {

            if (!diskQueue.isEmpty()) {
                Process closest = findShortestSeekTime(diskQueue);
                int target = closest.getPosition();

                int direction = Integer.compare(target, headPosition);

                while (headPosition != target) {
                    headPosition += direction;
                    headMovements++;
                    diskUpdate(processesQueue, diskQueue);
                }

                diskQueue.remove(closest);
                closest.setFinished(true);

            } else {
                diskUpdate(processesQueue, diskQueue);
            }
        }

        return headMovements;
    }

    public Process findShortestSeekTime(Queue<Process> diskQueue){

        Process closest = null;
        int shortestDistance = diskSize;

        for (Process p : diskQueue) {
            int distance = Math.abs(p.getPosition() - headPosition);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                closest = p;
            }
        }

        return closest;
    }

    public int processingForScan(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int headMovements = 0;
        System.out.printf("%-10s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        int direction = 1;
        while (!isEveryProcessFinished(processes)) {

            if (!diskQueue.isEmpty()) {

                headPosition += direction;
                headMovements++;
                diskUpdate(processesQueue, diskQueue);
                removeProcessAtThisPosition(diskQueue);

                if(headPosition >= diskSize){
                    direction = -1;
                }
                if(headPosition <= 0) {
                    direction = 1;
                }

            } else {
                diskUpdate(processesQueue, diskQueue);
            }
        }

        return headMovements;
    }

    public int processingForScanWithEDF(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        Queue<Process> realTimeProcessesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int headMovements = 0;
        int additionalHeadMovements = 0;
        int numberOfCompletedRTP = 0;
        int numberOfUncompletedRTP = 0;

        System.out.printf("%s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        int direction = 1;
        while (!isEveryProcessFinished(processes)) {

            if(realTimeProcessesQueue.isEmpty()){

                if (!diskQueue.isEmpty()) {

                    headPosition += direction;
                    headMovements++;
                    diskUpdate(processesQueue, diskQueue);
                    removeProcessAtThisPosition(diskQueue);

                    if(headPosition >= diskSize){
                        direction = -1;
                    }
                    if(headPosition <= 0) {
                        direction = 1;
                    }

                } else {
                    diskUpdate(processesQueue, diskQueue);
                }


            }else{

                ///jesli realTimeQueue nie jest pusta, to trzeba zająć się tymi procesami

                Process current = findShortestDeadlineTime(realTimeProcessesQueue);
                int target = current.getPosition();
                int extraDirection = Integer.compare(target, headPosition);

                while (headPosition != target) {
                    headPosition += extraDirection;
                    headMovements++;
                    additionalHeadMovements++;
                    diskUpdate(processesQueue, diskQueue);

                    ///zmniejszamy deadline kazdego realTimeProcess
                    for(Process p : realTimeProcessesQueue){
                        p.setId(p.getId()-1);
                    }
                }
                realTimeProcessesQueue.remove(current);
                if(current.getId() >= 0){
                    numberOfCompletedRTP++;
                }else{
                    numberOfUncompletedRTP++;
                }
            }

            ///Tutaj generuję real time processes z prawdopodobieństwem 1% co każdą iterację pętli
            int luckyNumber = 23;
            if(luckyNumber == (int)(Math.random()*100)){
                int position = (int)(Math.random()*diskSize);
                int deadline = (int)(Math.random()*300);
                realTimeProcessesQueue.add(new Process(deadline, localTime, position));
            }

        }

        System.out.printf("%40s %40s %40s", "[Completed real time processes: " + numberOfCompletedRTP + "]", "[Uncompleted real time processes: " + numberOfUncompletedRTP + "]", "[Additional head movements: " + additionalHeadMovements + "]");
        return headMovements;
    }

    public Process findShortestDeadlineTime(Queue<Process> diskQueue){

        Process closest = null;
        int shortestDeadline = diskSize;

        for (Process p : diskQueue) {
            if (p.getId() < shortestDeadline) {
                shortestDeadline = p.getId();
                closest = p;
            }
        }

        return closest;
    }

    public int processingForCScan(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int headMovements = 0;
        System.out.printf("%-10s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        while (!isEveryProcessFinished(processes)) {

            if (!diskQueue.isEmpty()) {

                headPosition++;
                headMovements++;
                if (headPosition >= diskSize) {
                    headPosition = 0;
                }
                diskUpdate(processesQueue, diskQueue);
                removeProcessAtThisPosition(diskQueue);

            } else {
                diskUpdate(processesQueue, diskQueue);
            }
        }

        return headMovements;
    }

    public int processingForCScanWithFDscan(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        Queue<Process> realTimeProcessesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int headMovements = 0;
        int additionalHeadMovements = 0;
        int numberOfCompletedRTP = 0;
        int numberOfUncompletedRTP = 0;

        System.out.printf("%-10s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        while (!isEveryProcessFinished(processes)) {

            if(realTimeProcessesQueue.isEmpty()){

                if (!diskQueue.isEmpty()) {

                    headPosition++;
                    headMovements++;
                    if (headPosition >= diskSize) {
                        headPosition = 0;
                    }
                    diskUpdate(processesQueue, diskQueue);
                    removeProcessAtThisPosition(diskQueue);

                } else {
                    diskUpdate(processesQueue, diskQueue);
                }
            }else{
                ///jesli realTimeQueue nie jest pusta, to trzeba zająć się tymi procesami
                ///najpierw usunac te ktore sa poza zasiegiem, a pozniej zajac sie tymi ktore sa

                numberOfUncompletedRTP = removeUnfeasibleProcesess(realTimeProcessesQueue, numberOfUncompletedRTP);

                if(!realTimeProcessesQueue.isEmpty()){
                    Process current = findShortestDeadlineTime(realTimeProcessesQueue);
                    int target = current.getPosition();
                    int extraDirection = Integer.compare(target, headPosition);

                    while (headPosition != target) {
                        headPosition += extraDirection;
                        headMovements++;
                        additionalHeadMovements++;
                        diskUpdate(processesQueue, diskQueue);

                        ///zmniejszamy deadline kazdego realTimeProcess
                        for(Process p : realTimeProcessesQueue){
                            p.setId(p.getId()-1);
                        }
                    }
                    realTimeProcessesQueue.remove(current);
                    numberOfCompletedRTP++;
                }
            }
            ///Tutaj generuję real time processes z prawdopodobieństwem 1% co każdą iterację pętli
            int luckyNumber = 23;
            if(luckyNumber == (int)(Math.random()*100)){
                int position = (int)(Math.random()*diskSize);
                int deadline = (int)(Math.random()*300);
                realTimeProcessesQueue.add(new Process(deadline, localTime, position));
            }

        }

        System.out.printf("%40s %40s %40s", "[Completed real time processes: " + numberOfCompletedRTP + "]", "[Uncompleted real time processes: " + numberOfUncompletedRTP + "]", "[Additional head movements: " + additionalHeadMovements + "]");
        return headMovements;
    }

    public int removeUnfeasibleProcesess(Queue<Process> realTimeProcessesQueue, int numberOfUncompletedRTP){
        Iterator<Process> iterator = realTimeProcessesQueue.iterator();
        while (iterator.hasNext()) {
            Process process = iterator.next();
            if (Math.abs(process.getPosition() - headPosition) > process.getId()) {
                iterator.remove();
                numberOfUncompletedRTP++;
            }
        }
        return numberOfUncompletedRTP;
    }

    public void removeProcessAtThisPosition(Queue<Process> diskQueue) {
        Iterator<Process> iterator = diskQueue.iterator();
        while (iterator.hasNext()) {
            Process process = iterator.next();
            if (process.getPosition() == headPosition) {
                iterator.remove();
                process.setFinished(true);
            }
        }
    }

    public void diskUpdate(Queue<Process> processesQueue, Queue<Process> diskQueue) {
        localTime++;

        while(!processesQueue.isEmpty() && processesQueue.peek().getArrivalTime() <= localTime){
            diskQueue.add(processesQueue.poll());
        }

    }

    public boolean isEveryProcessFinished(ArrayList<Process> processes) {
        for(Process process : processes) {
            if(!process.isFinished()) return false;
        }
        return true;
    }

}
