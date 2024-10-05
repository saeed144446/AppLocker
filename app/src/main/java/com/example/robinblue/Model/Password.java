package com.example.robinblue.Model;

import android.content.Context;

import io.paperdb.Paper;

public class Password {

    public String PASSWORD_KEY = "PASSWORD_KEY";
    public String STATUS_FIRST_STEP = "Draw an Unlock Pattern";
    public String STATUS_NEXT_STEP = "Draw a confirm Pattern";
    public String STATUS_PASSWORD_CORRECT = "Pattern set";
    public String STATUS_PASSWORD_INCORRECT = "Incorrect pattern";


    private boolean isFirststep = true;

    public void password(Context context) {
        Paper.init(context);

    }

    public void setPassword(String pwd) {
        Paper.book().write(PASSWORD_KEY, pwd);

    }

    public String getPassword() {
        return Paper.book().read(PASSWORD_KEY);
    }

    public boolean isFirststep() {
        return isFirststep;
    }

    public void setFirststep(boolean firststep) {
        isFirststep = firststep;
    }

    public boolean isCorrect(String pwd) {
        return pwd.equals(getPassword());
    }

    public boolean isResetPassword(boolean resetPassword) {
        if (resetPassword) {
            Paper.book().delete(PASSWORD_KEY);
            return true;
        } else {
            return false;
        }

    }
}
