/*
*  This is a menu driven program which has the functionality similar to that of a real world
 *  railway system.
 *  All the user and train credentials are stored in text-files.
* */

import java.io.*;
import java.util.*;

class Railway {
    String userName = "", phNumber = "";
    ArrayList<String> user = new ArrayList<>();
    ArrayList<String> phBook = new ArrayList<>();
    ArrayList<String> trainName = new ArrayList<>();
    ArrayList<String> trainCredentials = new ArrayList<>();
    int seatOccupied = 0;
    long bookingId = 0;
    boolean userRetreated = false, credentialsRetreated = false, firstTime = true;

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
    public String createRandomTime() {
        Random r = new Random();
        return Integer.toString(r.nextInt(24)) + ":" + Integer.toString(r.nextInt(60));
    }

    // displays the all of booking a user has
    public ArrayList<String> getAllBookings(String username) throws IOException {
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
            String key = details.substring(0, details.indexOf("-"));
            retreatFromLine(train, details);
            ArrayList<String> newList = new ArrayList<>();
            newList.addAll(trainName);
            train.put(key, newList);
            trainName.clear();
            firstTime = true;

        }
    }

    // Retreats users from text
    public void retreatUserFromText() throws IOException {
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
    public void retreatTrainCredentials() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("./Files/trainCredentials.txt")));
        String credentials;
        while ((credentials = br.readLine()) != null) {
            trainCredentials.add(credentials);
        }
        credentialsRetreated = true;
        br.close();
    }

    // Adds a new user into the existing list of username in a text file
    public void addUser() throws IOException {
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
    public boolean exists() throws IOException {
        if (!userRetreated)
            retreatUserFromText();

        for (String names : user)
            if (names.equalsIgnoreCase(this.userName)) {
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

    public boolean seatNumberExists(String credentials) throws IOException {
        if (!credentialsRetreated)
            retreatTrainCredentials();
        for (String i : trainCredentials) {
            String i_sub = (i.length() != 0) ? i.substring(i.indexOf("-") + 1, i.lastIndexOf("-")) : "\0";
            String credentials_sub = credentials.substring(0, credentials.lastIndexOf("-"));
            if (i_sub.equalsIgnoreCase(credentials_sub))
                seatOccupied++;
            if ((i.length() != 0) ? credentials.equalsIgnoreCase(i.substring(i.indexOf("-") + 1)) : false)
                return true;
        }
        return false;
    }

    // Booking a ticket for the passenger
    boolean isValidTrain = false;

    public boolean bookTrains(String key, Hashtable<String, ArrayList<String>> trains) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Select the trains from the following options and enter the name of the train");
        for (int i = 0; i < trains.get(key).size(); i++) {
            System.out.println(trains.get(key).get(i));
        }

        String selectedTrain = "";
        System.out.println("Departure time : " + createRandomTime());
        System.out.println("Arrival time : " + createRandomTime());

        // checking the validity of the train that the user has provided.
        while (true) {
            selectedTrain = sc.nextLine();
            for (int i = 0; i < trains.get(key).size(); i++) {
                if (selectedTrain.equalsIgnoreCase(trains.get(key).get(i))) {
                    isValidTrain = true;
                    break;
                }
            }
            if (isValidTrain)
                break;
            else
                System.out.println("Name of the Train was Incorrect. Please Try again");
        }
        System.out.println("Type AC for AC-Class or SL for Sleeper-Class");
        String classSelected = (sc.nextLine().equalsIgnoreCase("AC")) ? "AC-Class" : "Sleeper-Class";
        // creating a random seat number
        Random r = new Random();
        int seatNumber = r.nextInt(375) + 1;

        String credentials = "Station_" + key.charAt(0) + "-" + "Station_" + key.charAt(1) + "-" + selectedTrain + "-"
                + Integer.toString(seatNumber);
        while (seatNumberExists(credentials) && seatOccupied != 375)
            seatNumber = r.nextInt(375) + 1;
        if (seatOccupied == 375)
            System.out.println("All seats have been reserved");
        System.out.println(this.userName + "Your seatNumber number is" + seatNumber
                + "\n Do you want to confirm your booking ? Yes or No");
        boolean confirmed = (sc.nextLine().equalsIgnoreCase("yes")) ? true : false;

        if (confirmed) {
            // generate a new booking id by adding 1 to the previous booking id
            if (!credentialsRetreated)
                retreatTrainCredentials();
            int lastIndex = trainCredentials.size() - 1;
            if (trainCredentials.size() != 0) {
                bookingId = Long.parseLong(
                        trainCredentials.get(lastIndex).substring(trainCredentials.get(lastIndex).indexOf("_") + 1,
                                trainCredentials.get(lastIndex).indexOf("-")));
            } else {
                bookingId = 0;
            }
            // updating the credentials with the booking id
            credentials = this.userName + "_" + Long.toString(bookingId + 1) + "-" + credentials;
            // add the seatNumber, station name and train name to the text file
            addTrainCredentials(credentials);
        }
        return confirmed;
    }

    // main function for booking a ticket.
    public boolean ticketBooking(Hashtable<String, ArrayList<String>> trains) throws IOException { // main function for
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose from the menu which station you are on"
                + "\nStation_A, Station_B or Station_C and then enter the station name");
        String curStation = sc.nextLine().toUpperCase();
        System.out.println("Choose from the menu which station you want to go to"
                + "\nStation_A, Station_B or Station_C and then enter the station name");
        String nextStation = sc.nextLine().toUpperCase();
        // Displaying the trains available
        String switchCondition = Character.toString(curStation.charAt(curStation.length() - 1))
                + Character.toString(nextStation.charAt(nextStation.length() - 1));
        System.out.println(switchCondition);
        switch (switchCondition) {
        case "AB":
            return (bookTrains("AB", trains)) ? true : false;
        case "AC":
            return (bookTrains("AC", trains)) ? true : false;
        case "BA":
            return (bookTrains("BA", trains)) ? true : false;
        case "BC":
            return (bookTrains("BC", trains)) ? true : false;
        case "CA":
            return (bookTrains("CA", trains)) ? true : false;
        case "CB":
            return (bookTrains("CB", trains)) ? true : false;
        default:
            System.out.println("Invalid Input");
            return false;
        }
    }

    // Cancelling a ticket
    public boolean cancelTicket(long bookingID) throws IOException {
        if (!credentialsRetreated)
            retreatTrainCredentials();
        String bookingId = String.valueOf(bookingID);
        bookingId = this.userName + "_" + bookingId;
        System.out.println("Booking id: " + bookingId);
        for (int i = 0; i < trainCredentials.size(); i++) {
            String entries = trainCredentials.get(i);
            if (bookingId.equals(entries.substring(0, entries.indexOf("-")))) {
                System.out.println(entries.substring(0, entries.indexOf("-")));
                trainCredentials.remove(entries);
                i--;
                seatOccupied--;
                addTrainCredentials("");
                return true;
            }
        }
        return false;
    }

    // asking the users to enter their credentials
    public void promptForCredentials(String condition) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the username");
        this.userName = sc.next();
        boolean userExists = exists();
        // Check whether the user already exists or not
        if (!userExists && condition.equalsIgnoreCase("Booking")) {
            System.out.println("Enter the phone Number");
            while (true) {
                phNumber = sc.next();
                if (phNumber.length() == 10)
                    break;
                else
                    System.out.println("The phone number you entered is not valid");
            }
            phBook.add(phNumber);
        } else if (!userExists && condition.equalsIgnoreCase("Cancellation")) {
            // User request for cancellation is not valid
            System.out.println("You need to have a booking");
        } else if (userExists && condition.equalsIgnoreCase("Cancellation")) {
            System.out.println("Enter your booking number");
            bookingId = sc.nextInt();
        }
    }

    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        Railway passenger = new Railway();
        Hashtable<String, ArrayList<String>> trains = new Hashtable<String, ArrayList<String>>();
        passenger.retreatTrainDetails(trains);

        while (true) {
            // clearing up the console first
            clearConsole();
            System.out.println("Which of the following would you like to do" + "\n" + "Booking a ticket --> 1"
                    + "\nCancelling a ticket --> 2" + "\nSee all your bookings --> 3" + "\nExit --> 4");

            switch (sc.nextInt()) {
            // Booking
            case 1:
                passenger.promptForCredentials("booking");
                if (passenger.ticketBooking(trains))
                    if (!passenger.exists())
                        passenger.addUser();
                break;
            // Cancellation
            case 2:
                passenger.promptForCredentials("Cancellation");
                while (true)
                    if (passenger.cancelTicket(passenger.bookingId)) {
                        System.out.println("Your ticket has been cancelled");
                        System.out.println("Press any key to return back to the main menu");
                        String returnToMenu = sc.next();
                        break;
                    } else {
                        System.out.println("Something went wrong. Please try again");
                        passenger.promptForCredentials("Cancellation");
                    }

                break;
            // shows all your bookings
            case 3:
                passenger.promptForCredentials("Bookings");
                ArrayList<String> bookings = new ArrayList<>();
                if ((bookings = passenger.getAllBookings(passenger.userName)).size() != 0) {
                    System.out.println("Your bookings :-" + "\n");
                    for (String booking : bookings)
                        System.out.println(booking + "\n");
                    System.out.println("Press any key to return back to the main menu");
                    String returnToMenu = sc.next();

                } else {
                    System.out.println("User name was incorrect");
                }
                break;
            case 4:
                System.exit(0);
            }
        }
    }
}
