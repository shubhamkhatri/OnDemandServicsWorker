
package com.shubham.ondemandservicsworker.model;

public class transactionList {

    private String Amount,Date;

    public String getAmount() {
        return Amount;
    }

    public String getDate() {
        return Date;
    }

    public transactionList(String defaultAmount, String defaultDate) {
        Amount=defaultAmount;
        Date=defaultDate;
    }

}
