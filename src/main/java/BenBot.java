import java.util.Scanner;

public class BenBot {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("____________________________________________________________\n" +
                " What's good! I'm BenBot \n" +
                " What can I do for you today? \n" +
                "____________________________________________________________\n");

        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Cya soon! :)");
                System.out.println("____________________________________________________________");
                break;
            }

            System.out.println("____________________________________________________________");
            System.out.println(" " + input);
            System.out.println("____________________________________________________________");
            System.out.println();
        }

        sc.close();
    }


}
