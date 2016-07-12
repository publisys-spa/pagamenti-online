package it.publisys.pagamentionline.controller.rest.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author mcolucci
 * @param <T>
 */
public class ValidatorRequest<T> {

    private Validator validator;
    private List<String> errors;

    public ValidatorRequest() {
        _init();
    }

    private void _init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        errors = new ArrayList<>();
    }

    private void addError(String msg) {
        errors.add(msg);
    }

    public boolean validate(T bean) {
        errors.clear();

        boolean _isValid = true;

        if (bean == null) {
            _isValid = false;
            
            String _msg = "Richiesta effettuata non valida. Nessun dato inviato.";
            addError(_msg);
        } else {
            Set<ConstraintViolation<T>> _errors = validator.validate(bean);

            if (!_errors.isEmpty()) {
                _isValid = false;

                _errors.stream().forEach((_cv) -> {

                    String _msg = String.format("|%s - %s (invalid value: %s)|",
                        _cv.getPropertyPath().toString(),
                        _cv.getMessage(),
                        _cv.getInvalidValue());

                    addError(_msg);

                });

            }
        }

        return _isValid;
    }

    public List<String> getErrors() {
        return errors;
    }

}
