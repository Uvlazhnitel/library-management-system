package org.example.library.model;

public class Librarian extends User {

    private String employeeId;

    public Librarian() {
    }

    public Librarian(String id, String name, String email, String employeeId) {
        super(id, name, email);
        this.employeeId = employeeId;
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", employeeId='" + employeeId + '\'' +
                '}';
    }
}
