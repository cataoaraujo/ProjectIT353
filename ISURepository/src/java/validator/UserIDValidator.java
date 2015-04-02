
package validator;

import Model.User;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("userIDValidator")
public class UserIDValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String userID = value.toString();
        if (userID != null && !userID.isEmpty()) {
            User u = new User();
            u.setUserID(userID);
            if (u.verifyUserID()==false) {
                throw new ValidatorException(new FacesMessage("UserID already Used."));
            }
        }
    }
}
