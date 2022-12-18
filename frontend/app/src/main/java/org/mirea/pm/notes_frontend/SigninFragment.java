package org.mirea.pm.notes_frontend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mirea.pm.notes_frontend.databinding.FragmentSigninBinding;
import org.mirea.pm.notes_frontend.util_storage.JwtStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SigninFragment extends Fragment {

    private org.mirea.pm.notes_frontend.databinding.FragmentSigninBinding binding;

    private void setLoading(boolean loading)
    {
        if(loading) {
            binding.editTextUsername.setEnabled(false);
            binding.editTextPassword.setEnabled(false);
            binding.submitButton.setEnabled(false);
            binding.errorLine.setEnabled(false);
            binding.signupButton.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.editTextUsername.setEnabled(true);
            binding.editTextPassword.setEnabled(true);
            binding.submitButton.setEnabled(true);
            binding.errorLine.setEnabled(true);
            binding.signupButton.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void preRequest() {
        setLoading(true);
    }

    private void request(String username, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            requireActivity().runOnUiThread(this::preRequest);

            String protocol = getString(R.string.protocol);
            String socket = getString(R.string.socket);
            String error = "";
            try {
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

                try(OutputStream ostream = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    ostream.write(input, 0, input.length);
                }

                switch (connection.getResponseCode())
                {
                    case 200:
                        processResponse(connection.getInputStream());
                        break;
                    case 400:
                    case 401:
                        error = getString(R.string.bad_request_signin_error_message);
                        break;
                    default:
                        error = getString(R.string.default_network_error_message);
                        break;
                }

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

    private void processResponse(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(
                stream, StandardCharsets.UTF_8);
        JsonReader jsonReader = new JsonReader(reader);

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            if(jsonReader.nextName().equals("token")) {
                String token = jsonReader.nextString();

                JwtStorage.save(requireContext(), token);
            }
            else {
                jsonReader.skipValue();
            }
        }
    }

    public static SigninFragment newInstance(String param1, String param2) {
        return new SigninFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSigninBinding.inflate(inflater);

        binding.signupButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.signupFragment);
        });

        binding.submitButton.setOnClickListener(view -> {
            String username = binding.editTextUsername.getText().toString();
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
            binding.errorLine.setText("");

            request(username, password);

        });

        return binding.getRoot();
    }
}