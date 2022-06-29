package com.example.gumastha.Model;

import com.google.firebase.auth.FirebaseUser;

public interface ILogin {
    public void SignIn();
    public void GoToHome(FirebaseUser user);
}
