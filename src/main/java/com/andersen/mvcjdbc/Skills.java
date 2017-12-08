package com.andersen.mvcjdbc;

class Skills {
    private int ID;
    private String specialty;

    Skills(int ID, String specialty) {
        this.ID = ID;
        this.specialty = specialty;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
