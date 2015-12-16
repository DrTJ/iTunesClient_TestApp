package ir.royapajoohesh.itunesclient;

import ir.royapajoohesh.itunesclient.data.Users;
import ir.royapajoohesh.itunesclient.data.UsersDataSource;
import ir.royapajoohesh.utils.LanguageManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends ActivityBase {
	UsersDataSource usersDataSource;
	EditText UsernameEditText;
	EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		LanguageManager.ChangeTo(pref.getString("Language", "en"), this, false);

		setContentView(R.layout.activity_login);

		this.usersDataSource = new UsersDataSource(this);
		this.UsernameEditText = (EditText) findViewById(R.id.UsernameEditText);
		this.passwordEditText = (EditText) findViewById(R.id.passwordEditText);

		findViewById(R.id.RegisterButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			}
		});

		/* TEMP */
		startActivity(new Intent(this, MainActivity.class));
		finish();

	}

	public void LoginButton_Click(View v) {
		String username = this.UsernameEditText.getText().toString();
		String password = this.passwordEditText.getText().toString();

		if (username.length() == 0) {
			Toast.makeText(this, "Please enter your Username!", Toast.LENGTH_LONG).show();
			return;
		}

		if (password.length() == 0) {
			Toast.makeText(this, "Please enter your Password!", Toast.LENGTH_LONG).show();
			return;
		}

		this.usersDataSource.Open();

		// check if the user exist
		ArrayList<Users> dupUsers = this.usersDataSource.Select("Username = '" + username + "' and Password = '" + password
				+ "'", "Username");

		if (dupUsers.size() > 0) {
			startActivity(new Intent(this, MainActivity.class));
			this.usersDataSource.Close();
			finish();
		} else {
			Toast.makeText(this, "The username and/or password is incorrect!", Toast.LENGTH_LONG).show();
		}

		this.usersDataSource.Close();
	}

}
