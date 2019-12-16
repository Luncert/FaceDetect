package org.luncert.facedetect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Student {

    @Id
    private String id;

    private String name;

    @Column(columnDefinition = "MediumBlob")
    private byte[] faceData;
}
