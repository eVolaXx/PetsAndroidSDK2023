package petsnetwork.juanka.android;

public class PerrosAdopcionInfo {

    public String name, user, location, time, dog_image, profileimage, date, race, uid, age, description, comportamiento, contact;

    public PerrosAdopcionInfo() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public String getDogImage() {
        return dog_image;
    }

    public void setDogImage(String dog_image) {
        this.dog_image = dog_image;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public PerrosAdopcionInfo(String name, String dog_image, String profileimage, String time, String date, String user, String location, String uid, String race, String age, String description, String comportamiento, String contact) {
        this.name = name;
        this.profileimage = profileimage;
        this.date = date;
        this.time = time;
        this.dog_image = dog_image;
        this.uid = uid;
        this.user = user;
        this.location = location;
        this.race = race;
        this.age = age;
        this.description = description;
        this.comportamiento = comportamiento;
        this.contact = contact;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDogName() {
        return name;
    }

    public void setDog_name(String name) {
        this.name = name;
    }

    public String getEdad() {
        return age;
    }

    public void setEdad(String age) {
        this.age = age;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComportamiento() {
        return comportamiento;
    }

    public void setComportamiento(String comportamiento) {
        this.comportamiento = comportamiento;
    }

    public String getContacto() {
        return contact;
    }

    public void setContacto(String contact) {
        this.contact = contact;
    }


}
