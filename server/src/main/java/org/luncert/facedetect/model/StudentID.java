package org.luncert.facedetect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentID {

    /**
     * 年级
     */
    private Integer level;

    /**
     * 学院ID
     */
    private Integer collegeID;

    /**
     * 专业ID
     */
    private Integer majorID;

    private Integer classID;

    private Integer classSeq;

    public static StudentID parseString(String s) {
        if (s == null || s.length() != 13) {
            throw new IllegalArgumentException("Invalid student id.");
        }
        StudentID id = new StudentID();
        id.setLevel(Integer.valueOf(s.substring(0, 4)));
        id.setCollegeID(Integer.valueOf(s.substring(4, 6)));
        id.setMajorID(Integer.valueOf(s.substring(6, 8)));
        id.setClassID(Integer.valueOf(s.substring(8, 10)));
        id.setClassSeq(Integer.valueOf(s.substring(10, 13)));
        return id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(level)
                .append(collegeID);
        if (majorID < 10) {
            builder.append('0');
        }
        builder.append(majorID);
        if (classID < 10) {
            builder.append('0');
        }
        builder.append(classID);
        if (classSeq < 10) {
            builder.append("00");
        } else if (classSeq < 100) {
            builder.append('0');
        }
        builder.append(classSeq);
        return builder.toString();
    }
}