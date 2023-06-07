package petsnetwork.juanka.android;

public class Comentarios {

    public String comentario, fecha, time, usuario, profileimage;

    public Comentarios() {

    }

    public Comentarios(String comentario, String fecha, String time, String usuario, String profileimage) {
        this.comentario = comentario;
        this.profileimage = profileimage;
        this.fecha = fecha;
        this.time = time;
        this.usuario = usuario;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
