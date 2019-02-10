package model;


import com.avaje.ebean.Model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by chandu on 10/02/19.
 */
@Entity
@Table(name = "user")
public class User extends Model implements Serializable {

    public static Model.Find<Long, User> find = new Model.Find<Long, User>() {
    };


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    String name;
    String mobile;
    String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
