package com.andersen.mvcjdbc;

import java.util.Set;

class Developers {
    private int ID, salary;
    private String name;
    private Set<Skills> skills;

    Developers(int ID, String name, int salary, Set<Skills> skills) {
        this.ID = ID;
        this.salary = salary;
        this.name = name;
        this.skills = skills;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Skills> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skills> skills) {
        this.skills = skills;
    }
}
