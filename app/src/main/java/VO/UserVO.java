package VO;

/**
 * Created by misconstructed on 2018. 8. 23..
 */

public class UserVO {
    private String name;
    private String email;
    private String id;
    private String password;

    public UserVO(String name, String email, String id, String password) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"" +
                ", \"email\":\"" + email + "\"" +
                ", \"ID\":\"" + id + "\"" +
                ", \"password\":\"" + password + "\"" +
                "}";
    }
}
