package ir.royapajoohesh.itunesclient;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import ir.royapajoohesh.itunesclient.data.Users;
import ir.royapajoohesh.itunesclient.data.UsersDataSource;

public class RegisterActivity extends ActivityBase {

	UsersDataSource usersDataSource;

	private EditText UsernameEditText;
	private EditText passwordEditText;
	private EditText passwordConfirmationEditText;
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText MobileEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		this.usersDataSource = new UsersDataSource(this);

		this.UsernameEditText = (EditText) findViewById(R.id.UsernameEditText);
		this.passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		this.passwordConfirmationEditText = (EditText) findViewById(R.id.passwordConfirmationEditText);
		this.firstNameEditText = (EditText) findViewById(R.id.firstNameEditText1);
		this.lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		this.emailEditText = (EditText) findViewById(R.id.emailEditText);
		this.MobileEditText = (EditText) findViewById(R.id.MobileEditText);
	}

	public void RegisterButton_Click(View v) {
		Users newUser = new Users();

		String passwordConfirmation = this.passwordConfirmationEditText.getText().toString();

		newUser.Username = this.UsernameEditText.getText().toString();
		newUser.Password = this.passwordEditText.getText().toString();
		newUser.FirstName = this.firstNameEditText.getText().toString();
		newUser.LastName = this.lastNameEditText.getText().toString();
		newUser.Email = this.emailEditText.getText().toString();
		newUser.Mobile = this.MobileEditText.getText().toString();

		if (newUser.Username.length() == 0) {
			Toast.makeText(this, "Please enter Username!", Toast.LENGTH_LONG).show();
			return;
		}

		if (newUser.Password.length() == 0) {
			Toast.makeText(this, "Please enter Password!", Toast.LENGTH_LONG).show();
			return;
		}

		if (passwordConfirmation.equals(newUser.Password) == false) {
			Toast.makeText(this, "The password does not match the confirmation!", Toast.LENGTH_LONG).show();
			return;
		}

		if (newUser.FirstName.length() == 0) {
			Toast.makeText(this, "Please enter FirstName!", Toast.LENGTH_LONG).show();
			return;
		}

		if (newUser.LastName.length() == 0) {
			Toast.makeText(this, "Please enter LastName!", Toast.LENGTH_LONG).show();
			return;
		}

		if (newUser.Email.length() == 0) {
			Toast.makeText(this, "Please enter Email!", Toast.LENGTH_LONG).show();
			return;
		}

		if (newUser.Mobile.length() == 0) {
			Toast.makeText(this, "Please enter Mobile!", Toast.LENGTH_LONG).show();
			return;
		}

		this.usersDataSource.Open();
		// check if the user exist
		ArrayList<Users> dupUsers = this.usersDataSource.Select("Username = '" + newUser.Username + "'", "Username");

		if (dupUsers.size() > 0) {
			Toast.makeText(this, "Another user with this username already exist!", Toast.LENGTH_LONG).show();
			return;
		}

		this.usersDataSource.Insert(newUser);
		this.usersDataSource.Close();

		Toast.makeText(this, "Registered!", Toast.LENGTH_LONG).show();
	}
}
