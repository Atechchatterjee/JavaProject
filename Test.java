import java.io.*;
import java.util.*;
import java.util.ArrayList;

class Test {
    ArrayList<String> trainName = new ArrayList<>();
    boolean firstTime = true;
    String key;

    // public void addTolist(String trains) {
    // if (trains.length() == 0)
    // return;
    // if (!firstTime)
    // train.add(trains.substring(0, trains.indexOf("-")));
    // else
    // key = trains.substring(0, trains.indexOf("-"));
    // firstTime = false;

    // addTolist(trains.substring(trains.indexOf("-") + 1));
    // }
    public void printList(ArrayList<String> list) {
        System.out.println("The elements in the given list");
        for (String i : list)
            System.out.println(i);
    }

    public void retreatFromLine(Hashtable<String, ArrayList<String>> trains, String details) {
        String key = "";
        if (details.length() == 0) {
            return;
        }
        if (firstTime)
            key = details.substring(0, details.indexOf("-"));
        else
            trainName.add(details.substring(0, details.indexOf("-")));

        firstTime = false;
        retreatFromLine(trains, details.substring(details.indexOf("-") + 1));
    }

    public void retreatTrainDetails(Hashtable<String, ArrayList<String>> train) throws IOException {
        Scanner sc = new Scanner(new File("./Files/TrainDetails.txt"));
        while (sc.hasNext()) {
            String details = sc.nextLine() + "-";
            key = details.substring(0, details.indexOf("-"));
            retreatFromLine(train, details);
            ArrayList<String> newList = new ArrayList<>();
            newList.addAll(trainName);
            train.put(key, newList);
            trainName.clear();
            firstTime = true;

        }
    }

    public static void main(String args[]) throws IOException {
        Test test = new Test();
        // String trains = "AB-T1-T2-T3-T4-";
        // test.addTolist(trains);
        // System.out.println(test.key);
        // for (String i : test.train)
        // System.out.println(i);
        Hashtable<String, ArrayList<String>> trains = new Hashtable<String, ArrayList<String>>();
        test.retreatTrainDetails(trains);
        // for (String i : trains.get("AB"))
        // System.out.println(i);

    }
}