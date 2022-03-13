package service;
import domain.*;
import domain.Event;
import utils.Constants;
import utils.Nod;
import utils.Tuple;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import utils.events.*;
import utils.observer.Observable;
import utils.observer.Observer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.lang.Math.max;

public class SocialNetworkService implements Observable<PageChangeEvent> {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    EventsService eventsService;
    private int idEvent;
    Group groups = new Group();
    private int lastUpdate = 0;
    private Integer lastMessageIdUpdate=0;


    /**
     * Constructor
     * @param userService - the service for the users
     * @param friendshipService - the service for the friendships
     */
    public SocialNetworkService(UserService userService, FriendshipService friendshipService, MessageService messageService, EventsService eventsService)  {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventsService = eventsService;
        idEvent = eventsService.getLastEventID();
    }

//-----------------------------------------Functions for Users----------------------------------------------------------

    /**
     * @param s - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    public User findOneEntity(String s) {
        return userService.findOneEntity(s);
    }

    /**
     * @return all entities
     */
    public Iterable<User> findAllEntities() {
        return userService.findAllEntities();
    }
    /**
     * @return all entities from index "from" to index "to"
     */
    public Iterable<User> findAllEntities(int from, int to) {
        return userService.findAllEntities(from,to);
    }
    /**
     * @param attributes - the list of attributes of the object
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the user has already been saved in the memory
     */
    public void saveEntity(List<String> attributes) {
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4));
        user.setId(attributes.get(0));
        userService.saveEntity(user);
        notifyObservers(new PageChangeEvent(ChangeEventType.ADD, user));
    }

    /**
     *  removes the entity with the specified id
     * @param s
     *      id must be not null
     * @throws IllegalArgumentException
     *             if the given id is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    public void deleteEntity(String s) {
        User user = userService.deleteEntity(s);
        notifyObservers(new PageChangeEvent(ChangeEventType.DELETE, user));
    }

    /**
     * @param attributes - the list of attributes of the object
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    public void updateEntity(List<String> attributes) {
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4));
        user.setId(attributes.get(0));
        user.setPathToPicture(attributes.get(5));
        userService.updateEntity(user);
        User oldUser = userService.findOneEntity(user.getId());
        notifyObservers(new PageChangeEvent(ChangeEventType.UPDATE, user, oldUser));
    }

    /**
     *  Function verifies if a user exists or not
     * @param user email of user
     */
    public void verify(String user) {
        if(findOneEntity(user)==null)
            throw new RepositoryException("The user doesn't exist!!");
    }

    public void logIn(String mail, String password){
       User user = findOneEntity(mail);
        if(user == null)
            throw new RepositoryException("The user doesn't exist!!");
        if(!User.sha_256(password + user.getTimestamp()).equals(user.getPassword()))
            throw new RepositoryException("Password incorrect!!");
    }

    public void addObserverUser(Observer<UserChangeEvent> e){
        userService.addObserver(e);
    }

//----------------------------------Functions for Friendships-----------------------------------------------------------

    /**
     * Function return all the friendships from a period
     * @param user the id for user
     * @param dateStart dateStart
     * @param dateEnd dateEnd
     * @return  List of Friendships requiest
     */
    public List<Friendship> getListAllFriendshipsUserTimeInterval(String user, LocalDate dateStart, LocalDate dateEnd) {
        ArrayList<Friendship> list = new ArrayList<>();
        for(Friendship f :friendshipService.findAllEntities()){list.add(f);}
        return list.stream()
                .filter(x->x.getStatus().equals(Status.ACCEPTED))
                .filter(x -> x.getId().first.equals(user) || x.getId().second.equals(user))
                .filter(x -> (dateStart.compareTo(x.getDate()) <= 0) &&
                        (x.getDate().compareTo(dateEnd) <= 0)).toList();
    }

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    public Friendship findOneRelation(Tuple<String, String> id) {
        return friendshipService.findOneEntity(id);
    }

    /**
     * @return all entities
     */
    public Iterable<Friendship> findAllRelations() {
        return friendshipService.findAllEntities();
    }

    /**
     * @param attributes - the list of attributes of the object
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the users do not exist
     *             if the friendship has already been saved in the memory
     */
    public void saveRelation(List<String> attributes) {
        if(userService.findOneEntity(attributes.get(0)) == null ||
                userService.findOneEntity(attributes.get(0)) == null )
            throw new RepositoryException("The users do not exist!");
        Friendship friendship = getFriendship(attributes);
        friendshipService.saveEntity(friendship);
        notifyObservers(new PageChangeEvent(ChangeEventType.ADD, friendship));
    }

    /**
     *  removes the friendship with the specified id
     * @param id
     *      id must be not null
     * @throws IllegalArgumentException
     *             if the given id is null.
     * @throws RepositoryException
     *             if the friendship does not exist
     */
    public void deleteRelation(Tuple<String, String> id) {
        Friendship friendship = friendshipService.deleteEntity(id);
        notifyObservers(new PageChangeEvent(ChangeEventType.DELETE, friendship));
    }

    /**
     * @param attributes - the list of attributes of the object
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to updateusers
     */
    public void updateRelation(List<String> attributes) {
        Friendship friendship = getFriendship(attributes);
        Friendship oldFriendship = friendshipService.findOneEntity(friendship.getId());
        friendshipService.updateEntity(friendship);
        notifyObservers(new PageChangeEvent(ChangeEventType.UPDATE, friendship, oldFriendship));
    }

    /**
     * Function that creates a Friendship from a list of attributes
     * @param attributes - List of string, the attributes of the friendship
     * @return - a Friendship object
     */
    private Friendship getFriendship(List<String> attributes) {
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(attributes.get(0), attributes.get(1)));
        friendship.setStatus(Status.valueOf(attributes.get(2)));
        friendship.setDate(LocalDate.now());
        return friendship;
    }

    /**
     * Function finds all the user's friends
     * @param mail the user
     * @return Stream of string, all the friends and the date friendship starts
     */
    public Stream<String> getFriendsForOneUser(String mail) {
        ArrayList<Friendship> list = new ArrayList<>();
        for(Friendship f :friendshipService.findAllEntities()){list.add(f);}
        return list.stream()
                .filter(x->x.getStatus().equals(Status.ACCEPTED))
                .filter(x -> x.getId().first.equals(mail) || x.getId().second.equals(mail))
                .map(x -> {
                    if (Objects.equals(x.getId().first, mail)) {
                        User u = userService.findOneEntity(x.getId().second);
                        return u.getFirstName() + "|" + u.getLastName() + "|" + x.getDate();
                    } else {
                        User u = userService.findOneEntity(x.getId().first);
                        return u.getFirstName() + "|" + u.getLastName() + "|" + x.getDate();
                    }
                });
    }

    /**
     * Function finds all the user's friends
     * @param mail the user
     * @return Stream of Users, all the friends and the date friendship starts
     */
    public Stream<User> getFriendsForUser(String mail) {
        ArrayList<Friendship> list = new ArrayList<>();
        for(Friendship f :friendshipService.findAllEntities()){list.add(f);}
        return list.stream()
                .filter(x->x.getStatus().equals(Status.ACCEPTED))
                .filter(x -> x.getId().first.equals(mail) || x.getId().second.equals(mail))
                .map(x -> {
                    if (Objects.equals(x.getId().first, mail)) {
                        return userService.findOneEntity(x.getId().second);
                    } else {
                        return userService.findOneEntity(x.getId().first);
                    }
                });
    }

    /**
     * Function finds all the user's friends
     * @param mail the user
     * @return Stream of Users, all the friends and the date friendship starts
     */
    public Stream<String> getFriendsMailForUser(String mail) {
        ArrayList<Friendship> list = new ArrayList<>();
        for(Friendship f :friendshipService.findAllEntities()){list.add(f);}
        return list.stream()
                .filter(x->x.getStatus().equals(Status.ACCEPTED))
                .filter(x -> x.getId().first.equals(mail) || x.getId().second.equals(mail))
                .map(x -> {
                    if (Objects.equals(x.getId().first, mail))
                        return x.getId().second;
                    else
                        return x.getId().first;

                });
    }

    /**
     * Function finds all the user's friends from a month specified
     * @param mail the user
     *             mail must not be null
     * @param month the month friendship should start
     * @return Stream of string, all the friends and the date friendship starts
     */
    public Stream<String> getFriendsForOneUserFromMonth(String mail, String month) {
        List<String> months= List.of("1","2","3","4","5","6","7","8","9","10","11","12");
        if(months.contains(month))
          return getFriendsForOneUser(mail).filter(x-> x.split("\\|")[2].split("-")[1].equals(String.valueOf(month)));
        else
            throw new RepositoryException("Luna invalida!!\n");
    }

    /**
     * Function finds all the friend request
     * @param mail user
     *        mail must not be null
     * @return list of friend request between user with mail introduced and
     */
    public Stream<Friendship> getFriendRequest(String mail) {
        ArrayList<Friendship> list = new ArrayList<>();
        for(Friendship f :friendshipService.findAllEntities()){list.add(f);}
        return list.stream()
                .filter(x-> x.getId().second.equals(mail))
                .filter(x-> x.getStatus().equals(Status.PENDING));
    }

    /**
     * Function finds all the friend request
     * @param mail user
     *        mail must not be null
     * @return list of friend request between user with mail introduced and
     */
    public Stream<Friendship> getSentFriendRequests(String mail) {
        ArrayList<Friendship> list = new ArrayList<>();
        for(Friendship f :friendshipService.findAllEntities()){list.add(f);}
        return list.stream()
                .filter(x-> x.getId().first.equals(mail))
                .filter(x-> x.getStatus().equals(Status.PENDING));
    }

    /**
     * Function update as accepted a friend request or delete it
     * @param attributes - the attributes of the friend request
     */
    public void acceptFriendRequest(List<String> attributes) {
        Friendship friendship = getFriendship(attributes);
        if(Objects.equals(attributes.get(2), "ACCEPTED")) {
            friendshipService.updateEntity(friendship);
            notifyObservers(new PageChangeEvent(ChangeEventType.UPDATE, friendship, friendship));
        }
        else if(Objects.equals(attributes.get(2), "REJECTED")) {
            friendshipService.deleteEntity(new Tuple<>(attributes.get(0),attributes.get(1)));
            notifyObservers(new PageChangeEvent(ChangeEventType.DELETE, friendship));
        }
        else
            throw new RepositoryException("Operatia nu a putut fi efectuata!");
    }
    /**
     * Method that gets the number of friendships for a User for each month in a specific period
     * @param user String, representing the ID of the User
     * @return Map<String, Integer>, representing the Map containing pairs of Month)
     */
    public Map<String, Integer> getFriendshipToUserYear(String user, LocalDate start, LocalDate end) {
        List<Friendship> messageList = getListAllFriendshipsUserTimeInterval(user,start,end);
        Map<Integer, Integer> mapMessages = new HashMap<>();
        Map<String, Integer> map = new HashMap<>();
        Constants.months.forEach(month -> {
            long numberMessages = messageList.stream()
                    .filter(message ->  message.getDate().getMonthValue() == month)
                    .count();
            mapMessages.put(month, (int) numberMessages);
        });
        for(int i=1;i<=12;i++){
            map.put(Constants.convertIntToMonth(i), mapMessages.get(i));
        }
        return map;
    }

//----------------------------------Functions for Messages--------------------------------------------------------------

    /**
     * @param i - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    public Message findOneMessage(Integer i) {
        return messageService.findOneEntity(i);
    }

    /**
     * @return all entities
     */
    public Iterable<Message> findAllMessages() {
        return messageService.findAllEntities();
    }

    /**
     * @param attributes - the list of attributes of the object
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the user has already been saved in the memory
     */
    public Message saveMessage(List<String> attributes) {
        Message message = getMessage(attributes);
        messageService.saveEntity(message);
        notifyObservers(new PageChangeEvent(ChangeEventType.ADD, message));
        return message;
    }

    /**
     * Method that gets the number of messages sent to a User for each month in a specific period
     * @return Map<String, Integer>, representing the Map containing pairs of Month
     */
    public Map<String, Integer> getMessagesMap(List<Message> list) {
        Map<Integer, Integer> mapMessages = new HashMap<>();
        Map<String, Integer> map = new HashMap<>();
        Constants.months.forEach(month -> {
            long numberMessages =   list.stream()
                    .filter(message ->  message.getDate().getMonthValue() == month)
                    .count();
            mapMessages.put(month, (int) numberMessages);
        });
        for(int i=1;i<=12;i++){
            map.put(Constants.convertIntToMonth(i), mapMessages.get(i));
        }
        return map;
    }

    /**
     *  removes the entity with the specified id
     * @param i
     *      id must be not null
     * @throws IllegalArgumentException
     *             if the given id is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    public void deleteMessage(Integer i) {
        Message message = messageService.deleteEntity(i);
        notifyObservers(new PageChangeEvent(ChangeEventType.DELETE, message));
    }

    /**
     * @param attributes - the list of attributes of the object
     * @throws ValidationException
     *            if the entity is not valid
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    public void updateMessage(List<String> attributes) {
        Message message = getMessage(attributes);
        Message oldMessage = messageService.findOneEntity(message.getId());
        messageService.updateEntity(message);
        notifyObservers(new PageChangeEvent(ChangeEventType.UPDATE, message, oldMessage));
    }

    public void setGroups(String user){
        List<Message> messages = new ArrayList<>();
        User user1 = findOneEntity(user);
        for(Message m : messageService.findAllEntities()){
             messages.add(m);
        }
//        if(messages.size()==lastUpdate)
//            return;
        groups = null;
        groups = new Group();
        messages = messages.stream().sorted(Comparator.comparing(Message::getId)).toList();
        for(Message message: messages ){
//            if(message.getId()>lastMessageIdUpdate && ( message.getFrom().getId().equals(user) || message.getTo().contains(user1) )){
            if(message.getFrom().getId().equals(user) || message.getTo().contains(user1) ){
                List<User> list = new ArrayList<>(message.getTo());
                list.add(message.getFrom());
                list = list.stream().sorted(Comparator.comparing(Entity::getId)).toList();
                if(groups.containsKey(list)) {
                    List<Message> l = groups.get(list);
                    l.add(message);
                    groups.replace(list,l);
                }else {
                    List<Message> l = new ArrayList<>();
                    l.add(message);
                    groups.put(list, l);
                }
            }
        }
        }


    /**
     * Function that returns a list of all the messages that a User has ever received
     * @param id - the id of the user
     * @return - a list of Messages
     */
    public List<Message> getMessagesForOneUser(String id){
        ArrayList<Message> list = new ArrayList<>();
        for(Message m : messageService.findAllEntities()){list.add(m);}
        return list.stream()
                .filter(x -> {
                    return x.getTo()
                            .stream()
                            .anyMatch(y -> y.getId().equals(id));
                })
                .collect(Collectors.toList());
    }

    /**
     * Function that returns a list of all the messages between two Users, in cronological order
     * @param email1 the id of the first user
     * @param email2 the id of the second user
     * @return - the list of messages
     */
    public List<Message> cronologicalMessages(String email1, String email2){
        Predicate<Message> sender1 = x -> x.getFrom().getId().equals(email1);
        Predicate<Message> sender2 = x -> x.getFrom().getId().equals(email2);
        Predicate<Message> receiver1 = x -> {
            return x.getTo()
                    .stream()
                    .anyMatch(y -> y.getId().equals(email1));
        };
        Predicate<Message> receiver2 = x -> {
            return x.getTo()
                    .stream()
                    .anyMatch(y -> y.getId().equals(email2));
        };
        ArrayList<Message> list = new ArrayList<>();
        for(Message m : messageService.findAllEntities()){list.add(m);}
        return list.stream()
                .filter((sender1.and(receiver2)).or(sender2.and(receiver1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }
    /**
     * Function that returns a list of all the messages between two Users, in cronological order
     * @return - the list of messages
     */
    public List<Message> cronologicalMessagesGroup(Group group){
       List<Message> messages = new ArrayList<>();
       return messages;
    }
    /**
     * Function that returns a last message between two Users
     * @param email1 the id of the first user
     * @param email2 the id of the second user
     * @return - messages
     */
    public Message getLastMessages(String email1, String email2){
        Predicate<Message> sender1 = x -> x.getFrom().getId().equals(email1);
        Predicate<Message> sender2 = x -> x.getFrom().getId().equals(email2);
        Predicate<Message> receiver1 = x -> {
            return x.getTo()
                    .stream()
                    .anyMatch(y -> y.getId().equals(email1));
        };
        Predicate<Message> receiver2 = x -> {
            return x.getTo()
                    .stream()
                    .anyMatch(y -> y.getId().equals(email2));
        };
        List<Message> list = new ArrayList<>();
        for(Message m : messageService.findAllEntities()){list.add(m);}
        list = list.stream()
                .filter((sender1.and(receiver2)).or(sender2.and(receiver1)))
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .collect(Collectors.toList());
        return list.get(0);
    }
    /**
     * Function returns groups
     * @return - Group
     */
    public Group getGroups(){
        return groups;
    }


    /**
     * Function return all the messages with a user from a period
     * @param user the id for user
     * @param friend the id for friend
     * @param dateStart dateStart
     * @param dateEnd dateEnd
     * @return  List of Message requiest
     */
    public List<Message> getListAllMessagesForUsersTimeInterval(String user, String friend, LocalDate dateStart, LocalDate dateEnd) {
        return getListAllMessagesToUserTimeInterval(user,dateStart,dateEnd).stream().filter(x->x.getFrom().getId().equals(friend) || x.getTo().contains(friend)).toList();
    }

    /**
     * Function return all the messages from a period
     * @param user the id for user
     * @param dateStart dateStart
     * @param dateEnd dateEnd
     * @return  List of Message requiest
     */
    public List<Message> getListAllMessagesToUserTimeInterval(String user, LocalDate dateStart, LocalDate dateEnd) {
        return getMessagesForOneUser(user)
                .stream()
                .filter(message -> (dateStart.compareTo(message.getDate().toLocalDate()) <= 0) &&
                        (message.getDate().toLocalDate().compareTo(dateEnd) <= 0))
                .collect(Collectors.toList());
    }

    /**
     * Function that returns a thread of messages
     * @param idOfMessage the last message in the conversation
     * @return a list of messages
     */
    public List<Message> getConversation(Integer idOfMessage){
        List<Message> conversation = new ArrayList<>();
        Message currentMessage = messageService.findOneEntity(idOfMessage);
        while (currentMessage != null){
            conversation.add(currentMessage);
            currentMessage = currentMessage.getReplyMessage();
        }
        Collections.reverse(conversation);
        return conversation;
    }

    /**
     * Function that returns a Message entity from a list of attributes
     * @param attributes - the attributes of the Message
     * @return - a Message object
     */
    private Message getMessage(List<String> attributes) {
        User userFrom = userService.findOneEntity(attributes.get(0));
        List<User> usersTo = new ArrayList<>();
        Arrays.asList(attributes.get(1).split(";")).forEach(x -> usersTo.add(userService.findOneEntity(x)));
        if(Integer.parseInt(attributes.get(4)) != -1) {
            Message replyMessage = messageService.findOneEntity(Integer.parseInt(attributes.get(4)));
            return new Message(userFrom, usersTo, attributes.get(2), LocalDateTime.parse(attributes.get(3)), replyMessage);
        }
        return new Message(userFrom, usersTo, attributes.get(2), LocalDateTime.parse(attributes.get(3)), null);
    }

    /**
     * Function that returns the list of users that a person would send a message to if the option ReplyAll is chosen
     * @param message - the message that we want to reply to
     * @param email - the email of the user that sends the message
     * @return - a string of the emails
     */
    public String getListOfSenders(Message message, String email){
        StringBuilder sendEmailsTo = new StringBuilder();
        message.getTo().forEach(x -> {
            if(!x.getId().equals(email))
                sendEmailsTo.append(x.getId()).append(";");
        });
        return sendEmailsTo.toString();
    }

    public void addObserverMessages(Observer<MessageChangeEvent> e){
        messageService.addObserver(e);
    }
    public void addObserverMessages(Observer<MessageChangeEvent> e, String user) {
        messageService.addObserver(e,user);
    }

//-------------------------------------Functions for the Events---------------------------------------------------------

    public Event findOneEvent(Integer id){
        return eventsService.findOneEntity(id);
    }

    public Iterable<Event> findAllEvents(){
        return eventsService.findAllEntities();
    }

    public void saveEvent(List<String> attributes){
        Event event = getEvent(attributes);
        eventsService.saveEntity(event);
    }

    public void saveParticipant(int idEvent, String emailUser, boolean isNotified){
        eventsService.saveParticipant(idEvent, emailUser, isNotified);
        Event event = eventsService.findOneEntity(idEvent);
        notifyObservers(new PageChangeEvent(ChangeEventType.ADD, event));
    }

    public void deleteEvent(Integer id){
        eventsService.deleteEntity(id);
    }

    public void deleteParticipant(int idEvent, String emailUser){
        eventsService.deleteParticipant(idEvent, emailUser);
    }

    public void updateEvent(List<String> attributes){
        User user = findOneEntity(attributes.get(1));
        Event event = new Event(user, attributes.get(2), attributes.get(3), LocalDate.parse(attributes.get(4)), LocalDate.parse(attributes.get(5)), attributes.get(6));
        event.setId(Integer.parseInt(attributes.get(0)));
        event.setPathToPicture(attributes.get(7));
        eventsService.updateEntity(event);
    }

    public void updateParticipant(int idEvent, String emailUser, boolean isNotified){
        eventsService.updateParticipant(idEvent, emailUser, isNotified);
    }

    public List<Tuple<User, Tuple <Event, Boolean>>> getParticipants(){
        return eventsService.getParticipantsToEvent();
    }

    public Tuple<String, Tuple <Integer, Boolean>> getParticipant(int idEvent, String emailUser){
        return eventsService.getParticipant(idEvent, emailUser);
    }

    private Event getEvent(List<String> attributes) {
        User user = findOneEntity(attributes.get(0));
        Event event = new Event(user, attributes.get(1), attributes.get(2), LocalDate.parse(attributes.get(3)), LocalDate.parse(attributes.get(4)), attributes.get(5));
        if(attributes.size() == 7)
            event.setPathToPicture(attributes.get(6));
        idEvent++;
        event.setId(idEvent);
        return event;
    }
    public Iterable<Event> findAllEvents(int from, int to) {
        return eventsService.findAllEntities(from,to);
    }


//----------------------------------Functions for Network (the graph)---------------------------------------------------

    /**
     * Function that finds all the comunities in the network
     * @return - a list of lists that contains the comunities in the graph
     */
    public List<ArrayList<User>> comunity(){

        List<ArrayList<User>> listOfComunities = new ArrayList<ArrayList<User>>();

        String firstNotInComunity = null;
        Map<String, Nod<String>> noduri = new HashMap<>();
        for(User user: userService.findAllEntities()) {
            noduri.put(user.getId(), new Nod<String>(user.getId()));
            firstNotInComunity = user.getId();
        }
        while (firstNotInComunity != null){
            ArrayList<User> list = new ArrayList<>();
            bfs(firstNotInComunity, noduri);
            firstNotInComunity = null;
            for(Nod<String> nod1: noduri.values()){
                if(nod1.distance == -1 && firstNotInComunity == null)
                    firstNotInComunity = nod1.index;
                else if(nod1.distance > -1) {
                    list.add(userService.findOneEntity(nod1.index));
                    nod1.distance = -2;
                }
            }
            listOfComunities.add(list);
        }
        return listOfComunities;
    }

    /**
     * Function that implements the Breadth-Frist search
     * @param nod_start - the start node for the bfs
     * @param noduri - the list of nodes
     */
    private void bfs(String nod_start, Map<String, Nod<String>> noduri){

        noduri.get(nod_start).color = Color.GREY;
        noduri.get(nod_start).distance = 0;

        Queue<Nod<String>> queue = new LinkedList<>();
        queue.add(noduri.get(nod_start));

        while (!queue.isEmpty()){
            Nod<String> nod = queue.element();
            queue.remove();
            for(Friendship vertex: friendshipService.findAllEntities()){
                if(vertex.getId().first.equals(nod.index) || vertex.getId().second.equals(nod.index)){
                    Nod<String> nod1;
                    if(vertex.getId().first.equals(nod.index))
                        nod1 = noduri.get(vertex.getId().second);
                    else nod1 = noduri.get(vertex.getId().first);

                    if(nod1.color == Color.WHITE){
                        noduri.put(nod1.index, new Nod<String>(nod1.index, nod.index, nod.distance + 1, Color.GREY));
                        queue.add(noduri.get(nod1.index));
                    }
                }
            }
            noduri.get(nod.index).color = Color.BLACK;
        }
    }

    /**
     * Function that finds the longest path in a graph
     * @param com - the list of users in the comunity
     * @return - integer, the longest path in the comunity
     */
    public int longestPathInComunity(ArrayList<User> com){
        int longestPath = 0;
        Map<String, Nod<String>> noduri = new HashMap<>();
        for(User user: com) {
            noduri = new HashMap<>();
            for(User user1: com)
                noduri.put(user1.getId(), new Nod<String>(user1.getId()));
            longestPath = max(pathInComunity(user.getId(), noduri), longestPath);
        }
        return longestPath;
    }

    /**
     * Function that finds the longest path from the root to any other node
     * @param nod_start - the start node
     * @param noduri - a map that contains the nodes in the graph
     * @return - integer, longest path
     */
    public int pathInComunity(String nod_start, Map<String, Nod<String>> noduri) {

        int maxDistance = 0;
        noduri.get(nod_start).distance = 0;

        Queue<Nod<String>> queue = new LinkedList<>();
        queue.add(noduri.get(nod_start));

        while (!queue.isEmpty()) {
            Nod<String> nod = queue.element();
            queue.remove();
            for (Friendship vertex : friendshipService.findAllEntities()) {
                if (vertex.getId().first.equals(nod.index) || vertex.getId().second.equals(nod.index)) {
                    Nod<String> nod1;
                    if (vertex.getId().first.equals(nod.index))
                        nod1 = noduri.get(vertex.getId().second);
                    else nod1 = noduri.get(vertex.getId().first);

                    if (nod.nodes.get(nod1.index) == null && nod1.distance < nod.distance + 1) {
                        nod1.parent = nod.index;
                        nod1.distance = nod.distance + 1;
                        maxDistance = max(maxDistance, nod1.distance);
                        nod1.nodes.clear();
                        nod1.nodes.putAll(nod.nodes);
                        nod1.nodes.put(nod1.index, nod);
                        noduri.put(nod1.index, nod1);
                        queue.add(noduri.get(nod1.index));
                    }
                }
            }
        }
        return maxDistance;
    }


    private final List<Observer<PageChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<PageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<PageChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(PageChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

}
