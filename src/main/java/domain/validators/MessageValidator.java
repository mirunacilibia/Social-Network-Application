package domain.validators;

import domain.Message;
import domain.User;

import java.util.Objects;

public class MessageValidator  implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {

        String error = "";
        if(entity.getMessage().isEmpty())
            error += "The message can't be empty!\n";
        if(entity.getFrom() == null)
            error += "You can't send a message from that person!\n";
        if(entity.getTo().isEmpty())
            error += "You have to send the message to someone!\n";
        for(User user: entity.getTo()) {
            if (user.getId() == null) {
                error += "You can't send a message from that person!\n";
                break;
            }
            if(Objects.equals(user.getId(), entity.getFrom().getId())) {
                error += "You can't send a message to yourself!\n";
                break;
            }
        }
        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
