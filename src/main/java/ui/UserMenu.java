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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserMenu{
    String user;
    private SocialNetworkService service;
    private BufferedReader read;

    public UserMenu(SocialNetworkService service,String user) {
        this.service = service;
        this.user = user;
        read = new BufferedReader( new InputStreamReader(System.in));
        service.verify(user);
    }
    /**
     * Function that prints the menu
     */
    public void printMenu() {
        String  menu =  "\n----------- Retea de socializare -----------\n";
                menu += "   1   - Actualizare cont\n";
                menu += "   2   - Stergere cont\n";
                menu += "   3   - Prietenii mei\n";
                menu += "   4   - Prieteniile dintr-o anumita luna\n";
                menu += "   5   - Trimite/Raspunde la mesaje\n";
                menu += "   6   - Conversatia cu un prieten\n";
                menu += "   7   - Istoric mesaj\n";
                menu += "   8   - Trimitere cerere de prietenie\n";
                menu += "   9   - Accepta/Refuza cereri de prietenie\n";
                menu += "   10  - Sterge prieten\n";
                menu += "   0   - Iesire\n";
                menu += "--------------------------------------------\n";
        System.out.println(menu);
    }
    /**
     * Function that handles the command
     * @return - integer, the number of the command
     */
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

    /**
     * Function that runs the user interface
     */
    public void runUI() {
        int command = 0;
        do {
            try {
                printMenu();
                command = chooseCommand();
                switch (command) {
                    case 1:
                        updateAccount();
                        break;
                    case 2:
                        deleteAccount();
                        System.out.println("La revedere!");
                        return;
                    case 3:
                        getFriendsForOneUser();
                        break;
                    case 4:
                        getFriendsSpecificMonth();
                        break;
                    case 5:
                        sendMessage();
                        break;
                    case 6:
                        getConversation();
                        break;
                    case 7:
                        getHistoryOfReplies();
                        break;
                    case 8:
                        sendFriendRequest();
                        break;
                    case 9:
                        acceptFriendRequest();
                        break;
                    case 10:
                        deleteFriend();
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
    private void deleteFriend() throws IOException {
        System.out.println("Email-ul celei de-a doua persoane: ");
        String mail2 = read.readLine();

        service.deleteRelation(new Tuple<>(user, mail2));
        System.out.println("Operatia a fost realizata cu succes!");
    }

    /**
     * Function that rpints a list of messages
     * @param messages - the messages that have to be printed
     */
    private void printMessages(List<Message> messages) {
        messages.forEach(System.out::println);
    }

    /**
     * Function that prints a thread of messages
     * @throws IOException if there is a problem with the items read
     */
    private void getHistoryOfReplies() throws IOException{
        System.out.println("ID-ul mesajului pentru care doriti sa vedeti istoricul raspunsurilor: ");
        String id = read.readLine();
        printMessages(service.getConversation(Integer.parseInt(id)));
    }

    /**
     * Function that prints the conversation between two users
     * @throws IOException if there is a problem with the items read
     */
    private void getConversation() throws IOException {
        System.out.println("Email-ul persoanei pentru care doriti sa vedeti conversatia: ");
        String mail = read.readLine();
        printMessages(service.cronologicalMessages(user, mail));
    }

    /**
     * Function that sends a message to other users
     * @throws IOException if there is a problem with the items read
     */
    private void sendMessage() throws IOException {
        System.out.println("Aveti mesajele: ");
        printMessages(service.getMessagesForOneUser(user));
        System.out.println("\n\n");
        System.out.println("Raspundeti la un mesaj? da/nu");
        String isReply = read.readLine();
        switch (isReply){
            case "da":
                replyToMessage();
                break;
            case "nu":
                sendNewMessage();
                break;
            default:
                throw new IllegalArgumentException("Nu inteleg ce ziceti!\n");
        }
    }

    /**
     * Function that sneds a new message (that is not a reply)
     * @throws IOException if there is a problem with the items read
     */
    private void sendNewMessage() throws IOException {
        System.out.println("--Ati ales optiunea de a trimite un mesaj nou--");
        String continua = null;
        StringBuilder sendEmails = new StringBuilder();
        do{
            System.out.println("Cui trimiteti mesaj?\nEmail: ");
            String sendTo = read.readLine();
            sendEmails.append(sendTo);
            System.out.println("Mai trimiteti acest mesaj cuiva? da/nu");
            continua = read.readLine();
            if(continua.equals("da"))
                sendEmails.append(";");
        }while (continua.equals("da"));
        System.out.println("Mesajul pe care doriti sa il trimiteti: ");
        String text = read.readLine();

        service.saveMessage(Arrays.asList(user, sendEmails.toString(), text, LocalDateTime.now().toString(), "-1"));
        System.out.println("Operatia a fost realizata cu succes!");
    }

    /**
     * Function that sends a reply to a message
     * @throws IOException if there is a problem with the items read
     */
    private void replyToMessage() throws IOException {
        System.out.println("--Ati ales optiunea de a raspunde la un mesaj--");
        System.out.println("La ce mesaj raspundeti?\nID mesaj: ");
        String idOfMessage = read.readLine();
        System.out.println("Trimiteti mesajul tuturor persoanelor care l-au primit? da/nu");
        String replyToAll = read.readLine();
        String sendEmails = "";
        if(replyToAll.equals("da"))
            sendEmails = service.getListOfSenders(service.findOneMessage(Integer.parseInt(idOfMessage)), user);
        sendEmails += service.findOneMessage(Integer.parseInt(idOfMessage)).getFrom().getId();
        System.out.println("Mesajul pe care doriti sa il trimiteti: ");
        String text = read.readLine();
        service.saveMessage(Arrays.asList(user, sendEmails, text, LocalDateTime.now().toString(), idOfMessage));
        System.out.println("Operatia a fost realizata cu succes!");
    }

    /**
     * Function accepts or rejects a friend request
     * @throws IOException if there is a problem with the items read
     */
    public void acceptFriendRequest() throws IOException {
        List<Friendship> list = service.getFriendRequest(user).collect(Collectors.toList());
        if(list.isEmpty()){
            System.out.println("Nu aveti nicio cerere de prietenie!\n");
            return;
        }
        list.forEach(System.out::println);
        System.out.println("Email-ul utilizatorului care va trimis cererea: ");
        String mail2 = read.readLine();
        System.out.println("ALEGE: ACCEPTED/ REJECTED\n>>>>?");
        String status = read.readLine();
        service.acceptFriendRequest(Arrays.asList(user, mail2, status));
        System.out.println("Operatia a fost realizata cu succes!");
    }
    /**
     * Function accepts or rejects a friend request
     * @throws IOException if there is a problem with the items read
     */
    public void sendFriendRequest() throws IOException {
        System.out.println("Email-ul celei de-a doua persoane: ");
        String mail2 = read.readLine();
        String status = "PENDING";
        service.saveRelation(Arrays.asList(user, mail2, status));
        System.out.println("Operatia a fost realizata cu succes!");
    }

    /**
     * Function deletes the account
     */
    public void deleteAccount() {
        service.deleteEntity(user);
        System.out.println("Operatia a fost realizata cu succes!");
    }
    /**
     * Function updates the account
     */
    public void updateAccount() throws IOException {
        try {
            System.out.println("Noul prenume: ");
            String firstName = read.readLine();
            System.out.println("Noul nume: ");
            String lastName = read.readLine();
            System.out.println("Noul numar de telefon: ");
            String phone = read.readLine();
            System.out.println("Noua parola: ");
            String password = read.readLine();
            if(password.length() < 12)
                throw new ValidationException("Password must be at least 12 characters!");
            service.updateEntity(Arrays.asList(user, firstName, lastName, phone, password));
            System.out.println("Operatia a fost realizata cu succes!");
        } catch (ValidationException | IllegalArgumentException | RepositoryException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Function returns friends of user
     * @throws IOException if it's a problem with the items read
     */
    public void getFriendsForOneUser() throws IOException {
        service.getFriendsForOneUser(user).forEach(System.out::println);
    }

    /**
     * Function returns friends of user since month introduced
     * @throws IOException  if it's a problem with the items read
     */
    public void getFriendsSpecificMonth() throws IOException {;
        System.out.println("Luna: ");
        String month = read.readLine();
        service.getFriendsForOneUserFromMonth(user,month).forEach(System.out::println);
    }
}
