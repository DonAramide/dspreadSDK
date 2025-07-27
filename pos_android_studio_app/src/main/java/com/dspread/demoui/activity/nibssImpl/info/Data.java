package com.dspread.demoui.activity.nibssImpl.info;


import com.dspread.demoui.activity.nibssImpl.model.Commu;
import com.dspread.demoui.activity.nibssImpl.model.Key;
import com.dspread.demoui.activity.nibssImpl.model.Others;
import com.dspread.demoui.activity.nibssImpl.model.Password;
import com.dspread.demoui.activity.nibssImpl.model.Terminal;
import com.dspread.demoui.activity.nibssImpl.model.Transaction;

public class Data {
    private Terminal terminal;
    private Commu commu;
    private Transaction transaction;
    private Key key;
    private Password password;
    private Others others;
    private String ptsp;
    private String contact;
    private String footer;
    private String printlogo;
    private String keepalivetimer;

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Commu getCommu() {
        return commu;
    }

    public void setCommu(Commu commu) {
        this.commu = commu;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public Others getOthers() {
        return others;
    }

    public void setOthers(Others others) {
        this.others = others;
    }

    public String getPtsp() {
        return ptsp;
    }

    public void setPtsp(String ptsp) {
        this.ptsp = ptsp;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getPrintlogo() {
        return printlogo;
    }

    public void setPrintlogo(String printlogo) {
        this.printlogo = printlogo;
    }

    public String getKeepalivetimer() {
        return keepalivetimer;
    }

    public void setKeepalivetimer(String keepalivetimer) {
        this.keepalivetimer = keepalivetimer;
    }

    @Override
    public String toString() {
        return "Data{" +
                "terminal=" + terminal +
                ", commu=" + commu +
                ", transaction=" + transaction +
                ", key=" + key +
                ", password=" + password +
                ", others=" + others +
                ", ptsp='" + ptsp + '\'' +
                ", contact='" + contact + '\'' +
                ", footer='" + footer + '\'' +
                ", printlogo='" + printlogo + '\'' +
                ", keepalivetimer='" + keepalivetimer + '\'' +
                '}';
    }
}
