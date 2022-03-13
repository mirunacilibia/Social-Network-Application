package domain.validators;

import domain.Friendship;
import domain.Status;
import domain.User;

public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Function that validates the Friendship
     * @param entity - the Friendship to be validated
     * @throws ValidationException - if the Friendship is not valid
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {

        String error = "";
        if (!entity.getId().first.matches("[a-z0-9]+[.\\-_]*[a-z0-9]+@[a-z]+[.\\-_]*[a-z]+\\.[a-z]{2,}"))
            error += "The first email is not valid!\n";
        if (!entity.getId().second.matches("[a-z0-9]+[.\\-_]*[a-z0-9]+@[a-z]+[.\\-_]*[a-z]+\\.[a-z]{2,}"))
            error += "The second email is not valid!\n";
        if(entity.getId().first.equals(entity.getId().second))
            error += "The emails can't be the same!\n";
        if (!error.equals(""))
            throw new ValidationException(error);
    }
}