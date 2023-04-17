package es.codeurjc.nexusapp.model;

import java.sql.Date;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
public class Message 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter private long id;

    @Getter private LocalDateTime date = LocalDateTime.now();

    @ManyToOne
    @Getter private User recipient;

    @ManyToOne
    @Getter private User sender;

    @Column(columnDefinition = "TEXT")
    @Getter @Setter private String textContent;

    public Message(long id, User recipient, User sender, String textContent)
    {
        this.id = id;
        this.recipient = recipient;
        this.sender = sender;
        this.textContent = textContent;
    }
}
