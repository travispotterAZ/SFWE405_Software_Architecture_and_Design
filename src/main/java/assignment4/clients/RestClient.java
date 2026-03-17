package assignment4.clients;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Scanner; //Input scanner
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import assignment4.model.Person;

public class RestClient {

    private static String addPersonURL = "http://localhost:8080/person/addPerson";  //rest endpoint fot POST
    private static HttpClient client = HttpClient.newHttpClient(); //used to Http requests
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //terminal scanner for inputs
        
        System.out.println();
        System.out.println("-------------------------------");
        System.out.println("Enter 'exit' in name input to quit");
        System.out.println("Birthday inputs are of form: yyyy-MM-DD");
        System.out.println("Role types: Student, Coach, Manager");
        System.out.println();

        while(true){
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            if(name.equals("exit")){break;}
            
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter birthdate: ");
            String birthday = scanner.nextLine();
            LocalDate birthdate;
            try{
                birthdate = LocalDate.parse(birthday);
            } catch (DateTimeParseException e){
                System.out.println("Invalid birthdate input.");
                System.out.println();
                continue;
            }
        
            System.out.print("Enter university: ");
            String university = scanner.nextLine();

            System.out.print("Enter person Role: ");
            String role = scanner.nextLine();
            Person.Role checkRole;
            try{
                checkRole = Person.Role.valueOf(role);
            } catch (IllegalArgumentException e){
                System.out.println("Invalid role input.");
                System.out.println();
                continue;
            }
            
            Person person = new Person();
            person.setName(name);
            person.setEmail(email);
            person.setBirthdate(birthdate);
            person.setUniversity(university);
            person.setRole(checkRole);

            // Buildding JSON boldy that is requested for a post request to the REST API
            String personJson = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "birthdate": "%s",
                    "university": "%s",
                    "role": "%s"
                }
                """, name, email, birthdate, university, role);

            // Sending the post request to the REST API with the person JSON body
            try {
                HttpRequest request = HttpRequest.newBuilder()                  //Builder object for making HttpRequest
                    .uri(new java.net.URI(addPersonURL))                        //Specifies target URL
                    .header("Content-Type", "application/json")     //Specifies JSON body
                    .POST(HttpRequest.BodyPublishers.ofString(personJson))      //Set method to POST and adds the JSON body
                    .build(); //done building
                
                //Sending request and recording the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //Checks if code range is error worthy
                if (response.statusCode() >= 200 && response.statusCode() < 300) { //out of this ranged is error
                    System.out.println("Person sent to REST API!");
                } else {
                    System.out.println("Error sending person to REST API:");
                    System.out.println("Status code: " + response.statusCode());
                }
                System.out.println("-------------------------------");
            
            } catch (Exception e) {
                System.out.println("Failed to send person to REST API: " + e.getMessage());
                System.out.println("-------------------------------");
                System.out.println();
            }
        }

        scanner.close();
        System.out.println("Exiting RestClient.");
    }
}
