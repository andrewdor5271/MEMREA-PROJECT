package org.mirea.pm.notes_frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mirea.pm.notes_frontend.databinding.FragmentSignupBinding;
import org.mirea.pm.notes_frontend.util.Constants;
import org.mirea.pm.notes_frontend.util_storage.JwtStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SignupFragment extends Fragment {

    private org.mirea.pm.notes_frontend.databinding.FragmentSignupBinding binding;

    private void setLoading(boolean loading)
    {
        if(loading) {
            binding.editTextUsername.setEnabled(false);
            binding.editTextPassword.setEnabled(false);
            binding.editTextRepeatPassword.setEnabled(false);
            binding.submitButton.setEnabled(false);
            binding.errorLine.setEnabled(false);
            binding.signinButton.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.editTextUsername.setEnabled(true);
            binding.editTextPassword.setEnabled(true);
            binding.editTextRepeatPassword.setEnabled(true);
            binding.submitButton.setEnabled(true);
            binding.errorLine.setEnabled(true);
            binding.signinButton.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void preRequest() {
        setLoading(true);
    }

    private String requestSignup(String username, String password) throws IOException {
            String protocol = getString(R.string.protocol);
            String socket = getString(R.string.socket);
            URL url = new URL(getString(R.string.url_signup, protocol, socket));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String json = new StringBuilder()
                    .append("{\"username\":\"")
                    .append(username)
                    .append("\",\"password\":\"")
                    .append(password)
                    .append("\"}")
                    .toString();

            connection.setConnectTimeout(Constants.TIMEOUT);

            try(OutputStream ostream = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                ostream.write(input, 0, input.length);
            }

            switch (connection.getResponseCode())
            {
                case 200:
                    return "";
                case 400:
                case 401:
                    return getString(R.string.bad_request_signup_error_message);
                default:
                    return getString(R.string.default_network_error_message);
            }
    }

    @SuppressLint("ApplySharedPref")
    private String requestSignin(String username, String password) throws IOException {
        String protocol = getString(R.string.protocol);
        String socket = getString(R.string.socket);
        URL url = new URL(getString(R.string.url_signin, protocol, socket));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String json = new StringBuilder()
                .append("{\"username\":\"")
                .append(username)
                .append("\",\"password\":\"")
                .append(password)
                .append("\"}")
                .toString();

        connection.setConnectTimeout(Constants.TIMEOUT);

        try(OutputStream ostream = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            ostream.write(input, 0, input.length);
        }

        if(connection.getResponseCode() != 200) {
            return getString(R.string.default_network_error_message);
        }
        processSigninResponse(connection.getInputStream());
        SharedPreferences.Editor editor =
                requireActivity().getSharedPreferences(
                        Constants.PREFS_NAME,
                        Context.MODE_PRIVATE).edit();
        editor.putString(Constants.USERNAME_PREF_NAME, username);
        editor.commit();
        return "";
    }

    private void requests(String username, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            requireActivity().runOnUiThread(this::preRequest);

            String error;
            try {
                error = requestSignup(username, password);
            } catch (IOException e) {
                error = getString(R.string.default_network_error_message);
                e.printStackTrace();
            }

            if(!error.equals("")) {
                String finalError = error;
                requireActivity().runOnUiThread(() -> postRequest(finalError));
                return;
            }

            try {
                error = requestSignin(username, password);
            } catch (IOException e) {
                error = getString(R.string.default_network_error_message);
                e.printStackTrace();
            }
            finally {
                String finalError = error;
                requireActivity().runOnUiThread(() -> postRequest(finalError));
            }
        });
    }

    private void postRequest(String error) {
        if(Objects.equals(error, "")) {
            requireActivity().finish();
        }
        setLoading(false);
        binding.errorLine.setText(error);
    }

    private void processSigninResponse(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(
                stream, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> parsed = mapper.readValue(reader, Map.class);

        JwtStorage.save(requireContext(), (String) parsed.get("token"));
    }

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance(String param1, String param2) {
        return new SignupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater);

        binding.signinButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.signinFragment);
        });

        binding.submitButton.setOnClickListener(view -> {
            String username = binding.editTextUsername.getText().toString();
            String confirmPassword = binding.editTextRepeatPassword.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            if (username.isEmpty()) {
                binding.errorLine.setText(R.string.auth_error_empty_username);
                return;
            }
            if (username.length() > 20) {
                binding.errorLine.setText(R.string.auth_error_long_username);
                return;
            }
            if (password.length() < 4) {
                binding.errorLine.setText(R.string.auth_error_short_password);
                return;
            }
            if (password.length() > 30) {
                binding.errorLine.setText(R.string.auth_error_long_password);
                return;
            }
            if(!password.equals(confirmPassword)) {
                binding.errorLine.setText(R.string.auth_error_password_match);
                return;
            }
            binding.errorLine.setText("");

            requests(username, password);
        });

        return binding.getRoot();
    }
}