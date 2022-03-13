package domain.validators;

public interface Validator<T>{

    /**
     * Function that validates an entity
     * @param entity - the entity to be validated, type T
     * @throws ValidationException - if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
