package ui;

import domain.Friendship;
import domain.Message;
import domain.User;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import service.SocialNetworkService;
import utils.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserInterface implements UI{

    private SocialNetworkService service;
    private BufferedReader read;

    public UserInterface(SocialNetworkService service) {
        this.service = service;
        read = new BufferedReader(
                new InputStreamReader(System.in));
    }

    @Override
    public void printMenu() {
        String  menu =  "\n------- Retea de socializare -------\n";
        menu += "   1  - Adaugare User\n";
        menu += "   2  - Stergere User\n";
        menu += "   3  - Cate comunitati exista?\n";
        menu += "   5  - Cea mai sociabila comunitate\n";
        menu += "   5  - Prietenii unei persoane\n";
        menu += "   6  - Afisare useri si prietenii\n";
        menu += "   7  - Adauga prieten\n";
        menu += "   8  - Sterge prieten\n";
        menu += "   9  - Modifica prietenie\n";
        menu += "   0  - Iesire\n";
        menu += "------------------------------------\n";
        System.out.println(menu);
    }

    @Override
    public int chooseCommand() {
        try {
            System.out.println("Alegeti comanda: ");
            String number = read.readLine();
            return Integer.parseInt(number);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void runUI() {
        int command = 0;
        do {
            try {
                printMenu();
                command = chooseCommand();
                switch (command) {
                    case 1:
                        saveEntity();
                        break;
                    case 2:
                        deleteEntity();
                        break;
                    case 3:
                        numberOfComunities();
                        break;
                    case 4:
                        mostSocialComunity();
                        break;
                    case 5:
                        getFriendsForOneUser();
                        break;
                    case 6:
                        findAllEntities();
                        System.out.println("\n");
                        findAllRelations();
                        System.out.println("\n");
                        break;
                    case 7:
                        addFriend();
                        break;
                    case 8:
                        deleteFriend();
                        break;
                    case 9:
                        updateFriendship();
                        break;
                    case 0:
                        System.out.println("La revedere!");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + command);
                }
            } catch (IllegalStateException e){
                System.out.println(e.getMessage());
                command = -1;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ValidationException | RepositoryException | IllegalArgumentException e){
                System.out.println(e.getMessage());
            }
        } while (command != 0);

    }


    /**
     * Function deletes a friend from friends list
     * @throws IOException if there is a problem with the items read
     */
    @Override
    public void deleteFriend() throws IOException {
        System.out.println("Email-ul primei persoane: ");
        String mail1 = read.readLine();
        System.out.println("Email-ul celei de-a doua persoane: ");
        String mail2 = read.readLine();

        service.deleteRelation(new Tuple<>(mail1, mail2));
        System.out.println("Operatia a fost realizata cu succes!");
    }


    @Override
    public void updateFriendship() throws IOException {
        System.out.println("Email-ul primei persoane: ");
        String mail1= read.readLine();
        System.out.println("Email-ul celei de-a doua persoane: ");
        String mail2 = read.readLine();
        System.out.println("Noul status: ");
        String status =read.readLine();
        service.saveRelation(Arrays.asList(mail1, mail2, status));
        System.out.println("Operatia a fost realizata cu succes!");
    }

    @Override
    public void addFriend() throws IOException {
        System.out.println("Email-ul primei persoane: ");
        String mail1= read.readLine();
        System.out.println("Email-ul celei de-a doua persoane: ");
        String mail2 = read.readLine();
        String status = "PENDING";
        service.saveRelation(Arrays.asList(mail1, mail2, status));
        System.out.println("Operatia a fost realizata cu succes!");
    }
    @Override
    public void findOneEntity() {}

    @Override
    public void findAllEntities() {
        service.findAllEntities().forEach(user -> System.out.println(user.toString()));
    }

    @Override
    public void saveEntity() throws IOException {
        System.out.println("Introduceti datele:");
        System.out.println("Email: ");
        String mail = read.readLine();
        System.out.println("Prenume: ");
        String firstName = read.readLine();
        System.out.println("Nume: ");
        String lastName = read.readLine();
        System.out.println("Numar de telefon: ");
        String phone = read.readLine();
        System.out.println("Parola: ");
        String password = read.readLine();

        if(password.length() < 12)
            throw new ValidationException("Password must be at least 12 characters!");

        service.saveEntity(Arrays.asList(mail, firstName, lastName, phone, password));
        System.out.println("Operatia a fost realizata cu succes!");
    }

    @Override
    public void deleteEntity() throws IOException {
        System.out.println("Introduceti email-ul persoanei pe care doriti sa o stergeti: ");
        String mail = read.readLine();

        service.deleteEntity(mail);
        System.out.println("Operatia a fost realizata cu succes!");
    }


    public void getFriendsForOneUser() throws IOException {
        System.out.println("Email-ul: ");
        String mail = read.readLine();
        service.getFriendsForOneUser(mail).forEach(System.out::println);
    }

    @Override
    public void findAllRelations() {
        service.findAllRelations().forEach(friendship -> System.out.println(friendship.toString()));
    }


    public void numberOfComunities(){
        List<ArrayList<User>> listOfComunities = service.comunity();
        int k = 1;
        for(ArrayList<User> comunity: listOfComunities){
            System.out.println("\nComunitatea " + k + ":");
            for(User user: comunity){
                System.out.printf("%-12s %-12s %-20s\n", user.getFirstName(), user.getLastName(), user.getId());
            }
            k++;
        }
        System.out.println("In retea avem " + (k - 1) + " comunitati");
    }

    public void mostSocialComunity(){
        int longestPath = 0;
        int index = 0;
        int path;
        List<ArrayList<User>> listOfComunities = service.comunity();
        for(int i = 0; i < listOfComunities.size(); i++){
            if((path = service.longestPathInComunity(listOfComunities.get(i))) > longestPath){
                longestPath = path;
                index = i;
            }
        }
        System.out.println("Cea mai sociabila comunitate este comunitatea " + (index + 1));
        for(User user: listOfComunities.get(index)){
            System.out.printf("%-12s %-12s %-20s\n", user.getFirstName(), user.getLastName(), user.getId());
        }
    }

}
