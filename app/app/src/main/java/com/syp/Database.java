package com.syp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.syp.model.User;

class TestUser {

    public String username;
    public String password;

    public TestUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public TestUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

}

public class Database {
    private DatabaseReference mDatabase;

    public Database() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void writeNewUser(String userId, String name, String email) {
        TestUser user = new TestUser(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
}
