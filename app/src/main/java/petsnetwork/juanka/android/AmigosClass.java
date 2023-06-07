package petsnetwork.juanka.android;

public class AmigosClass {
    public String profileimage, usuario, raza, mascota, fecha;

    public AmigosClass() {

    }

    public AmigosClass(String profileimage, String usuario, String fecha, String raza, String mascota) {
        this.profileimage = profileimage;
        this.fecha = fecha;
        this.usuario = usuario;
        this.raza = raza;
        this.mascota = mascota;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getMascota() {
        return mascota;
    }

    public void setMascota(String mascota) {
        this.mascota = mascota;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
