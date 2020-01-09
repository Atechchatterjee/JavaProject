/*
*  This class contains all the utility function which are required
*  for reading and writing data to & from the text files
* */

import java.io.*;
import java.util.*;

class Utilities {
    String userName = "", phNumber = "";
    ArrayList<String> user = new ArrayList<>();
    ArrayList<String> phBook = new ArrayList<>();
    ArrayList<String> trainName = new ArrayList<>();
    ArrayList<String> trainCredentials = new ArrayList<>();
    int seatOccupied = 0;
    long bookingId = 0;
    boolean userRetreated = false, credentialsRetreated = false;
    boolean firstTime = true;
    // clears up the console
    public static void clearConsole() {
        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (Exception E) {
                System.out.println(E);
            }
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }

    }

    // creates random time
    public static String createRandomTime() {
        Random r = new Random();
        return Integer.toString(r.nextInt(24)) + ":" + Integer.toString(r.nextInt(60));
    }

    // displays the all of booking a user has
    public static ArrayList<String> getAllBookings(String username) throws IOException {
        ArrayList<String> bookings = new ArrayList<>();
        if (!credentialsRetreated)
            retreatTrainCredentials();
        for (String entries : trainCredentials) {
            if (entries.substring(0, entries.indexOf("_")).equals(username))
                bookings.add(entries);
        }
        return bookings;
    }

    // Retreat Train name and fare
    public static void retreatFromLine(Hashtable<String, ArrayList<String>> trains, String details) {
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

    public static void retreatTrainDetails(Hashtable<String, ArrayList<String>> train) throws IOException {
        Scanner sc = new Scanner(new File("./Files/TrainDetails.txt"));
        while (sc.hasNext()) {
            String details = sc.nextLine() + "-";
            String key = details.substring(0, details.indexOf("-"));
            retreatFromLine(train, details);
            ArrayList<String> newList = new ArrayList<>();
            newList.addAll(trainName);
            train.put(key, newList);
            trainName.clear();
            firstTime = true;
        }
    }

    // Retreats user from text
    public static void retreatUserFromText() throws IOException {
        System.out.println("Reading from the file");
        // reads from the text file adds to the user list
        BufferedReader br = new BufferedReader(new FileReader(new File("./Files/users.txt")));
        String names;
        while ((names = br.readLine()) != null) {
            user.add(names.substring(0, names.indexOf("_")));
        }
        userRetreated = true;
        br.close();
    }

    // Retreats the train credentials from the text
    public static void retreatTrainCredentials() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("./Files/trainCredentials.txt")));
        String credentials;
        while ((credentials = br.readLine()) != null) {
            trainCredentials.add(credentials);
        }
        credentialsRetreated = true;
        br.close();
    }

    // Adds a new user into the existing list of username in a text file
    public static void addUser() throws IOException {
        if (!userRetreated)
            retreatUserFromText();

        // adds the username to the list
        user.add(userName);

        // writes to the text file
        FileWriter writer = new FileWriter("./Files/users.txt", true);

        writer.write(userName + "_" + phNumber + "\n");
        writer.close();
    }

    // To check whether the user exists or not
    public static boolean exists(String userName) throws IOException {
        if (!userRetreated)
            retreatUserFromText();

        for (String names : user)
            if (names.equalsIgnoreCase(userName)) {
                System.out.println("User exists");
                return true;
            }

        return false;
    }

    // adding the seat number, train name and station name to the text File
    boolean cancelled = false;

    public void addTrainCredentials(String credentials) throws IOException {
        if (!credentialsRetreated)
            retreatTrainCredentials();
        if (credentials != "")
            trainCredentials.add(credentials);
        else
            cancelled = true;

        // System.out.println(credentials);
        FileWriter writer = new FileWriter("./Files/trainCredentials.txt", !cancelled);
        if (!cancelled)
            writer.write(credentials + "\n");
        else
            for (String entries : trainCredentials)
                writer.write(entries + "\n");

        writer.close();
    }
}