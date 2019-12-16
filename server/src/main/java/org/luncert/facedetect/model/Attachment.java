package org.luncert.facedetect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    @Column(name = "attachmentName")
    private String name;

    @Column(name = "attachmentData", columnDefinition = "MediumBlob")
    private byte[] data;
}
