package domain.validators;

import domain.User;

public class UserValidator implements Validator<User>{

    /**
     * Function that validates the User
     * @param entity - the User to be validated
     * @throws ValidationException - if the entity is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {

        String error = "";
        if(!entity.getFirstName().matches("[A-Za-z]*"))
            error += "The first name is not valid!\n";
        if(!entity.getLastName().matches("[A-Za-z]*"))
            error += "The last name is not valid!\n";
        if(!entity.getPhoneNumber().matches("0[0-9]{9}"))
            error += "The phone number is not valid!\n";
        if(!entity.getId().matches("[a-z0-9]+[.\\-_]*[a-z0-9]+@[a-z]+[.\\-_]*[a-z]+\\.[a-z]{2,}"))
            error += "The email is not valid!\n";
        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
