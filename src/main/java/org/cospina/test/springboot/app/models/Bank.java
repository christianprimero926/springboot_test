package org.cospina.test.springboot.app.models;

public class Bank {
    private  Long id;
    private String name;
    private int totalTranfers;

    public Bank() {
    }

    public Bank(Long id, String name, int totalTranfers) {
        this.id = id;
        this.name = name;
        this.totalTranfers = totalTranfers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTranfers() {
        return totalTranfers;
    }

    public void setTotalTranfers(int totalTranfers) {
        this.totalTranfers = totalTranfers;
    }
}
