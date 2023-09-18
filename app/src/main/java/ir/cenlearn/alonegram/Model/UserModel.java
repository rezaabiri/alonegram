package ir.cenlearn.alonegram.Model;

/**
 * Created by Hetbo on 2/21/2018.
 */

public class UserModel {
    String username;
    String email;
    String imageUrl;
    String fullname;
    String phone;

    String bluetick;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname(){
        return fullname;
    }

    public void setFullname(String fullname){
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBluetick() {
        return bluetick;
    }

    public void setBluetick(String bluetick) {
        this.bluetick = bluetick;
    }
}
