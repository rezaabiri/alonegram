package ir.cenlearn.alonegram.Model;

public class ViewModel {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBluetick() {
        return bluetick;
    }

    public void setBluetick(String bluetick) {
        this.bluetick = bluetick;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String username;
    private String fullname;
    private String bluetick;
    private String image;

    public static String getView() {
        return view;
    }

    public static void setView(String view) {
        ViewModel.view = view;
    }

    private static String view;
}
