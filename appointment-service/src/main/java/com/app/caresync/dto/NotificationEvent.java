package com.app.caresync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long recipientId;
    private String recipientRole;
    private String title;
    private String message;
    private String type;
    private String channel;
    private Long relatedId;
    private String relatedType;
}
