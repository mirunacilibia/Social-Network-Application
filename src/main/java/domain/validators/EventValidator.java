package domain.validators;

import domain.Event;

public class EventValidator implements Validator<Event>{

    /**
     * Function that validates the Event
     * @param entity - the Event to be validated
     * @throws ValidationException - if the Event is not valid
     */
    @Override
    public void validate(Event entity) throws ValidationException {
        String error = "";
        if(entity.getName().isEmpty())
            error += "Name can't be empty!\n";
        if(entity.getStartDate().isAfter(entity.getEndDate()))
            error += "The start and end dates do not match!\n";
        if(entity.getLocation().isEmpty())
            error += "Location can't be empty!\n";
        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
