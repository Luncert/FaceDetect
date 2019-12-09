package org.luncert.facedetect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 4063489781520192803L;

    private String objectID;

    private UserRole role;

    public static UserAccount fromString(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Unable to parse null-pointer or empty string.");
        }
        char c = s.charAt(0);
        if (c == 't' || c == 'T') {
            return new UserAccount(s.substring(1), UserRole.Teacher);
        } else {
            return new UserAccount(s, UserRole.Student);
        }
    }

    @Override
    public String toString() {
        if (UserRole.Teacher.equals(role)) {
            return "T" + objectID;
        } else {
            // as student
            return objectID;
        }
    }
}