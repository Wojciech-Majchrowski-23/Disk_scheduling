package src;

import java.util.*;

public class DISK {

    private final int diskSize = 1000;
    private int localTime, headPosition;


    public DISK() {}

    public int firstComeFirstServed(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s", "[First_Come_First_Served]");
        return processingForFCFS(processes, diskQueue);
    }

    public int shortestSeekTimeFirst(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[Shortest_Seek_Time_First]");
        return processingForSSTF(processes, diskQueue);
    }

    public int scan(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[Scan]");
        return processingForScan(processes, diskQueue);
    }

    public int scanWithIrritatingProcesses(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[Scan with irritating processes]");
        return processingForScanWithIrritatingProcesses(processes, diskQueue);
    }

    public int scanWithEDF(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[Scan with EDF]");
        return processingForScanWithEDF(processes, diskQueue);
    }

    public int cScan(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[C-Scan]");
        return processingForCScan(processes, diskQueue);
    }

    public int cScanWithIrritatingProcesses(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[C-Scan with irritating processes]");
        return processingForCScanWithIrritatingProcesses(processes, diskQueue);
    }

    public int cScanWithFDscan(ArrayList<Process> processes) {
        localTime = -1;
        Queue<Process> diskQueue = new LinkedList<>();
        System.out.printf("%-45s","[C-Scan with FD-scan]");
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

                if(headPosition >= diskSize){
                    direction = -1;
                }
                if(headPosition <= 0) {
                    direction = 1;
                }
                diskUpdate(processesQueue, diskQueue);
                removeProcessAtThisPosition(diskQueue);

            } else {
                diskUpdate(processesQueue, diskQueue);
            }
        }

        return headMovements;
    }

    public int processingForScanWithIrritatingProcesses(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int irritatingProcessCounter = 0;
        int maxIrritatingProcesses = 1000;

        int headMovements = 0;
        System.out.printf("%-10s", "[starting head position: " + headPosition + "]");

        diskUpdate(processesQueue, diskQueue);

        int direction = 1;
        while (!isEveryProcessFinished(processes)) {

            if (!diskQueue.isEmpty()) {

                headPosition += direction;
                headMovements++;

                if(headPosition >= diskSize){
                    direction = -1;
                }
                if(headPosition <= 0) {
                    direction = 1;
                }
                // ------------------- extra feature ------------------------//
                // -----------adding new processes after head ---------------//

                if(headPosition > 0 && direction == 1 && irritatingProcessCounter < maxIrritatingProcesses) {
                    Process iritatingProcess = generateIrritatingProcess(direction);
                    if(iritatingProcess != null){
                        processesQueue.add(iritatingProcess);
                        processes.add(iritatingProcess);
                        irritatingProcessCounter++;
                    }
                }
                diskUpdate(processesQueue, diskQueue);
                removeProcessAtThisPosition(diskQueue);

            } else {
                diskUpdate(processesQueue, diskQueue);
            }
        }

        System.out.printf("%40s", "[Irritating processes generated: " + irritatingProcessCounter + "]");

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
            Process realTimeProcess = generateRealTimeProcess();
            if(realTimeProcess != null){
                realTimeProcessesQueue.add(realTimeProcess);
            }

        }

        System.out.printf("%40s %40s %40s", "[Completed real time processes: " + numberOfCompletedRTP + "]", "[Uncompleted real time processes: " + numberOfUncompletedRTP + "]", "[Additional head movements: " + additionalHeadMovements + "]");
        return headMovements;
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

    public int processingForCScanWithIrritatingProcesses(ArrayList<Process> processes, Queue<Process> diskQueue){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> processesQueue = new LinkedList<>(processes);
        headPosition = (int)(Math.random()*diskSize);

        int irritatingProcessCounter = 0;
        int maxIrritatingProcesses = 1000;

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

                // ------------------- extra feature ------------------------//
                // -----------adding new processes after head ---------------//

                if(headPosition > 0 && irritatingProcessCounter < maxIrritatingProcesses) {
                    Process iritatingProcess = generateIrritatingProcess(1);
                    if(iritatingProcess != null){
                        processesQueue.add(iritatingProcess);
                        processes.add(iritatingProcess);
                        irritatingProcessCounter++;
                    }
                }

                //-----------------------------------------------------------//

                diskUpdate(processesQueue, diskQueue);
                removeProcessAtThisPosition(diskQueue);

            } else {
                diskUpdate(processesQueue, diskQueue);
            }
        }

        System.out.printf("%40s", "[Irritating processes generated: " + irritatingProcessCounter + "]");

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

                numberOfUncompletedRTP = removeUnfeasibleProcesses(realTimeProcessesQueue, numberOfUncompletedRTP);

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
            Process realTimeProcess = generateRealTimeProcess();
            if(realTimeProcess != null){
                realTimeProcessesQueue.add(realTimeProcess);
            }

        }

        System.out.printf("%40s %40s %40s", "[Completed real time processes: " + numberOfCompletedRTP + "]", "[Uncompleted real time processes: " + numberOfUncompletedRTP + "]", "[Additional head movements: " + additionalHeadMovements + "]");
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

    public int removeUnfeasibleProcesses(Queue<Process> realTimeProcessesQueue, int numberOfUncompletedRTP){
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

    public Process generateRealTimeProcess(){
        int luckyNumber = 23;
        if(luckyNumber == (int)(Math.random()*100)){
            int position = (int)(Math.random()*diskSize);
            int deadline = (int)(Math.random()*300);
            Process realTimeProcess = new Process(deadline, localTime, position);
            return realTimeProcess;
        }
        return null;
    }

    public Process generateIrritatingProcess(int direction){
        int luckyNumber = 23;
        if(luckyNumber == (int)(Math.random()*100)){
            return new Process(0, localTime, headPosition + direction);
        }
        return null;
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
