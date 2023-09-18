package ir.cenlearn.alonegram.Model;

public class UserSearchModel {
    public String getuAvatar() {
        return uAvatar;
    }

    public void setuAvatar(String uAvatar) {
        this.uAvatar = uAvatar;
    }

    public String getuUsername() {
        return uUsername;
    }

    public void setuUsername(String uUsername) {
        this.uUsername = uUsername;
    }

    public String getuFullname() {
        return uFullname;
    }

    public void setuFullname(String uFullname) {
        this.uFullname = uFullname;
    }

    public String getuBluetick() {
        return uBluetick;
    }

    public void setuBluetick(String uBluetick) {
        this.uBluetick = uBluetick;
    }


    private String uAvatar;
    private String uUsername;
    private String uFullname;
    private String uBluetick;
}
