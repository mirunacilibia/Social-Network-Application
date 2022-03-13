package domain;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;

public class User extends Entity<String>{

    String firstName;
    String lastName;
    String phoneNumber;
    String password;
    String timestamp;
    String pathToPicture;

    /**
     * Constructor for the User
     * @param firstName - string - the user's first name
     * @param lastName - string - the user's last name
     * @param phoneNumber - string - the user's phone number
     * @param password - string - the user's password before encrypting
     */
    public User(String firstName, String lastName, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.timestamp = Timestamp.from(Instant.now()).toString();
        String psswd = password + timestamp;
        this.password = sha_256(psswd);
        this.pathToPicture = "photos/no_profile_picture.jpg";
    }


    /**
     * Constructor for the User - when we read the user from a file
     * @param firstName - string - the user's first name
     * @param lastName - string - the user's last name
     * @param phoneNumber - string - the user's phone number
     * @param timestamp - timestamp - the timestamp that we use for encrypting the password
     * @param password - string - the user's password after encrypting
     */
    public User(String firstName, String lastName, String phoneNumber, String timestamp, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.timestamp = timestamp;
        this.password = password;
        this.pathToPicture = "photos/no_profile_picture.jpg";
    }

    /**
     * Function that encrypts a string
     * @param string - the string that needs to be encrypted
     * @return string - the encrypted string
     */
    public static String sha_256(String string) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ignored) {}

        byte[] hash = md.digest(string.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    /**
     * Getter
     * @return - string, first name of the User
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter
     * @return - string, last name of the User
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter
     * @return - string, phone number of the User
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Getter
     * @return - timestamp, the timestamp used for encrypting the password
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Getter
     * @return - string, encrypted password of the User
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter
     * @return - string, te path to the profile picture
     */
    public String getPathToPicture() {
        return pathToPicture;
    }

    /**
     * Setter
     * @param firstName - string
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter
     * @param lastName - string
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Setter
     * @param phoneNumber - string
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Setter - sets a new password and sets a new timestamp
     * @param password - string, the encrypted password
     */
    public void setPassword(String password) {
        this.timestamp = Timestamp.from(Instant.now()).toString();
        this.password = sha_256(password + timestamp);
    }

    /**
     * Setter - sets a new profile picture
     * @param pathToPicture - string, the path to the profile picture
     */
    public void setPathToPicture(String pathToPicture) {
        this.pathToPicture = pathToPicture;
    }

    @Override
    public String toString() {
        return "User: " +
                "email = " + this.getId() + "\n\t" +
                "firstName = " + firstName + "\n\t" +
                "lastName = " + lastName + "\n\t" +
                "phoneNumber = " + phoneNumber + "\n";
    }
}
