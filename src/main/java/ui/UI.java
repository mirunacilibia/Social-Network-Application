package ui;

import java.io.IOException;

public interface UI {

    /**
     * Function that prints the menu
     */
    void printMenu();

    /**
     * Function that handles the command
     * @return - integer, the number of the command
     */
    int chooseCommand();

    /**
     * Function that runs the user interface
     */
    void runUI();

    /**
     * Function updates a friendship
     * @throws IOException if there is a problem with the items read
     */
    void updateFriendship() throws IOException;

    /**
     * Function accepts or rejects a friend request
     * @throws IOException if there is a problem with the items read
     */
    void addFriend() throws IOException;

    /**
     * Function deletes a friend from friends list
     * @throws IOException if there is a problem with the items read
     */
    void deleteFriend() throws IOException;

    /**
     * Function that finds a User
     */
    void findOneEntity();

    /**
     * prints all entities
     */
    void findAllEntities();

    /**
     * Function that saves a User
     */
    void saveEntity() throws IOException;

    /**
     * Function that deletes a User
     */
    void deleteEntity() throws IOException;


    /**
     * prints all friendships
     */
    void findAllRelations();

}
