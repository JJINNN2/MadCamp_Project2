package com.example.phonephoto.phone;


import android.telephony.PhoneNumberUtils;

import androidx.annotation.NonNull;

class PhoneItem {
    private int Id;
    private String Name;
    private String Number;
    //private boolean isSelected;

    public PhoneItem(int _Id, String _Name, String _PhoneNumber)
    {
        this.Id = _Id;
        this.Name = _Name;
        this.Number = _PhoneNumber.replaceAll("-", "");
        //this.isSelected = isSelected;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public String getNumber() {
        return PhoneNumberUtils.formatNumber(Number);
    }

    @NonNull
    @Override
    public String toString() {
        super.toString();
        return "name: " + Name + " phone: " + Number;
    }

    //public boolean isSelected() { return isSelected; }

//    public void setSelected(boolean isSelected) {
//        this.isSelected = isSelected;
//    }

    //    public String getId() { return id;}
}